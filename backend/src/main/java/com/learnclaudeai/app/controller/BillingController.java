package com.learnclaudeai.app.controller;

import com.learnclaudeai.app.entity.LifetimePurchase;
import com.learnclaudeai.app.entity.User;
import com.learnclaudeai.app.repository.LifetimePurchaseRepository;
import com.learnclaudeai.app.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private static final Logger log = LoggerFactory.getLogger(BillingController.class);

    private final UserRepository userRepository;
    private final LifetimePurchaseRepository lifetimePurchaseRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${stripe.price.monthly}")
    private String priceMonthly;

    @Value("${stripe.price.yearly}")
    private String priceYearly;

    @Value("${stripe.price.lifetime}")
    private String priceLifetime;

    @Value("${stripe.lifetime.max}")
    private int lifetimeMax;

    @Value("${site.url}")
    private String siteUrl;

    public BillingController(UserRepository userRepository,
                             LifetimePurchaseRepository lifetimePurchaseRepository) {
        this.userRepository = userRepository;
        this.lifetimePurchaseRepository = lifetimePurchaseRepository;
    }

    @PostConstruct
    public void init() {
        if (stripeSecretKey != null && !stripeSecretKey.isBlank()) {
            Stripe.apiKey = stripeSecretKey;
        }
    }

    @PostMapping("/create-checkout-session")
    @Transactional
    public ResponseEntity<?> createCheckoutSession(@RequestBody Map<String, String> body,
                                                    @AuthenticationPrincipal User authUser) {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Billing is not configured"));
        }

        String priceId = body.get("priceId");
        if (priceId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "priceId is required"));
        }

        // Fetch fresh user from DB (authUser from JWT may be stale)
        User user = userRepository.findById(authUser.getId()).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            // Ensure Stripe customer exists
            if (user.getStripeCustomerId() == null) {
                Customer customer = Customer.create(CustomerCreateParams.builder()
                        .setEmail(user.getEmail())
                        .putMetadata("userId", user.getId().toString())
                        .build());
                user.setStripeCustomerId(customer.getId());
                userRepository.save(user);
            }

            String stripePriceId;
            com.stripe.param.checkout.SessionCreateParams.Mode mode;

            switch (priceId) {
                case "lifetime" -> {
                    long lifetimeCount = lifetimePurchaseRepository.count();
                    if (lifetimeCount >= lifetimeMax) {
                        return ResponseEntity.status(HttpStatus.GONE)
                                .body(Map.of("error", "Lifetime plan is no longer available"));
                    }
                    stripePriceId = priceLifetime;
                    mode = com.stripe.param.checkout.SessionCreateParams.Mode.PAYMENT;
                }
                case "yearly" -> {
                    stripePriceId = priceYearly;
                    mode = com.stripe.param.checkout.SessionCreateParams.Mode.SUBSCRIPTION;
                }
                case "monthly" -> {
                    stripePriceId = priceMonthly;
                    mode = com.stripe.param.checkout.SessionCreateParams.Mode.SUBSCRIPTION;
                }
                default -> {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid priceId"));
                }
            }

            var params = com.stripe.param.checkout.SessionCreateParams.builder()
                    .setCustomer(user.getStripeCustomerId())
                    .setMode(mode)
                    .setSuccessUrl(siteUrl + "/dashboard?upgraded=true")
                    .setCancelUrl(siteUrl + "/pricing")
                    .putMetadata("userId", user.getId().toString())
                    .putMetadata("priceId", priceId)
                    .addLineItem(com.stripe.param.checkout.SessionCreateParams.LineItem.builder()
                            .setPrice(stripePriceId)
                            .setQuantity(1L)
                            .build());

            Session session = Session.create(params.build());

            return ResponseEntity.ok(Map.of("sessionUrl", session.getUrl()));
        } catch (Exception e) {
            log.error("Failed to create checkout session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create checkout session"));
        }
    }

    @PostMapping("/webhook")
    @Transactional
    public ResponseEntity<?> handleWebhook(@RequestBody String payload,
                                           @RequestHeader("Stripe-Signature") String sigHeader) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            log.error("Error parsing webhook", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parse error");
        }

        String type = event.getType();
        log.info("Stripe webhook: {}", type);

        try {
            switch (type) {
                case "checkout.session.completed" -> handleCheckoutCompleted(event);
                case "customer.subscription.deleted" -> handleSubscriptionDeleted(event);
                case "customer.subscription.updated" -> handleSubscriptionUpdated(event);
            }
        } catch (Exception e) {
            log.error("Error handling webhook {}: {}", type, e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/portal")
    public ResponseEntity<?> createPortalSession(@AuthenticationPrincipal User authUser) {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Billing is not configured"));
        }

        User user = userRepository.findById(authUser.getId()).orElse(null);
        if (user == null || user.getStripeCustomerId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No billing account found"));
        }

        try {
            var session = com.stripe.model.billingportal.Session.create(
                    com.stripe.param.billingportal.SessionCreateParams.builder()
                            .setCustomer(user.getStripeCustomerId())
                            .setReturnUrl(siteUrl + "/settings")
                            .build());
            return ResponseEntity.ok(Map.of("portalUrl", session.getUrl()));
        } catch (Exception e) {
            log.error("Failed to create portal session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create portal session"));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(@AuthenticationPrincipal User authUser) {
        User user = userRepository.findById(authUser.getId()).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(Map.of(
                "plan", user.getPlan().name(),
                "subscriptionStatus", user.getSubscriptionStatus() != null ? user.getSubscriptionStatus() : "none",
                "planExpiresAt", user.getPlanExpiresAt() != null ? user.getPlanExpiresAt().toString() : ""
        ));
    }

    @GetMapping("/lifetime-remaining")
    public ResponseEntity<?> getLifetimeRemaining() {
        long count = lifetimePurchaseRepository.count();
        return ResponseEntity.ok(Map.of(
                "sold", count,
                "remaining", Math.max(0, lifetimeMax - count),
                "available", count < lifetimeMax
        ));
    }

    private void handleCheckoutCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null) return;

        String userIdStr = session.getMetadata().get("userId");
        String priceId = session.getMetadata().get("priceId");
        if (userIdStr == null || priceId == null) return;

        Long userId = Long.parseLong(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        if (user.getStripeCustomerId() == null) {
            user.setStripeCustomerId(session.getCustomer());
        }

        if ("lifetime".equals(priceId)) {
            user.setPlan(User.Plan.LIFETIME);
            user.setSubscriptionStatus("lifetime");
            userRepository.save(user);

            LifetimePurchase purchase = new LifetimePurchase();
            purchase.setUserId(userId);
            lifetimePurchaseRepository.save(purchase);

            log.info("User {} upgraded to LIFETIME", user.getEmail());
        } else {
            user.setPlan(User.Plan.PRO);
            user.setSubscriptionId(session.getSubscription());
            user.setSubscriptionStatus("active");
            userRepository.save(user);

            log.info("User {} upgraded to PRO ({})", user.getEmail(), priceId);
        }
    }

    private void handleSubscriptionDeleted(Event event) {
        Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().orElse(null);
        if (subscription == null) return;

        User user = userRepository.findBySubscriptionId(subscription.getId()).orElse(null);
        if (user == null) return;

        user.setPlan(User.Plan.FREE);
        user.setSubscriptionStatus("canceled");
        user.setSubscriptionId(null);
        userRepository.save(user);

        log.info("User {} downgraded to FREE (subscription canceled)", user.getEmail());
    }

    private void handleSubscriptionUpdated(Event event) {
        Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().orElse(null);
        if (subscription == null) return;

        User user = userRepository.findBySubscriptionId(subscription.getId()).orElse(null);
        if (user == null) return;

        user.setSubscriptionStatus(subscription.getStatus());
        if ("active".equals(subscription.getStatus()) || "trialing".equals(subscription.getStatus())) {
            user.setPlan(User.Plan.PRO);
            if (subscription.getCurrentPeriodEnd() != null) {
                user.setPlanExpiresAt(Instant.ofEpochSecond(subscription.getCurrentPeriodEnd()));
            }
        }
        userRepository.save(user);

        log.info("User {} subscription updated: {}", user.getEmail(), subscription.getStatus());
    }
}
