<template>
  <div class="max-w-3xl mx-auto px-6 py-16">
    <h1 class="text-3xl font-bold text-charcoal mb-8">Privacy Policy</h1>
    <div class="prose text-charcoal/80 space-y-6 text-sm leading-relaxed">
      <p><strong>Last updated:</strong> {{ new Date().toLocaleDateString('en-US', { month: 'long', year: 'numeric' }) }}</p>

      <h2 class="text-lg font-semibold text-charcoal mt-8">1. Information We Collect</h2>
      <p><strong>Account information:</strong> When you register, we collect your email address and a hashed version of your password. We never store your password in plain text.</p>
      <p><strong>Payment information:</strong> Payment processing is handled entirely by Stripe. We do not store credit card numbers, bank account details, or other financial information on our servers. We store your Stripe customer ID to manage your subscription.</p>
      <p><strong>Usage data:</strong> We track lesson progress (which lessons you've completed) and chat messages sent to the AI tutor to provide the service. We may collect anonymized analytics data to improve the platform.</p>

      <h2 class="text-lg font-semibold text-charcoal mt-8">2. How We Use Your Information</h2>
      <p>We use your information to: provide and maintain the Service, process payments, send lesson notifications (if you opt in), improve the platform, and communicate important updates about your account.</p>

      <h2 class="text-lg font-semibold text-charcoal mt-8">3. AI Tutor Conversations</h2>
      <p>Messages you send to the AI tutor are processed by Anthropic's Claude API to generate responses. Your conversation history is sent with each message to maintain context but is not stored on our servers after the session ends. Anthropic may process this data according to their own privacy policy.</p>

      <h2 class="text-lg font-semibold text-charcoal mt-8">4. Data Sharing</h2>
      <p>We do not sell your personal information. We share data only with:</p>
      <ul class="list-disc pl-5 space-y-1">
        <li><strong>Stripe</strong> — for payment processing</li>
        <li><strong>Anthropic</strong> — for AI tutor functionality (conversation messages)</li>
        <li><strong>Resend</strong> — for email notifications (your email address, if opted in)</li>
      </ul>

      <h2 class="text-lg font-semibold text-charcoal mt-8">5. Data Security</h2>
      <p>We use industry-standard security practices including encrypted connections (HTTPS), bcrypt password hashing, and JWT-based authentication. However, no system is 100% secure, and we cannot guarantee absolute security.</p>

      <h2 class="text-lg font-semibold text-charcoal mt-8">6. Your Rights</h2>
      <p>You may: access your account data at any time, update your email or notification preferences, request deletion of your account and data by contacting support, and export your progress data.</p>

      <h2 class="text-lg font-semibold text-charcoal mt-8">7. Cookies</h2>
      <p>We use localStorage (not cookies) to store your authentication token for session management. We do not use tracking cookies or third-party analytics cookies.</p>

      <h2 class="text-lg font-semibold text-charcoal mt-8">8. Children's Privacy</h2>
      <p>The Service is not intended for children under 13. We do not knowingly collect personal information from children under 13.</p>

      <h2 class="text-lg font-semibold text-charcoal mt-8">9. Changes to This Policy</h2>
      <p>We may update this Privacy Policy from time to time. We will notify registered users of material changes via email.</p>

      <h2 class="text-lg font-semibold text-charcoal mt-8">10. Contact</h2>
      <p>Questions about this Privacy Policy? Contact us at support@learnclaudeai.com.</p>
    </div>
  </div>
</template>
