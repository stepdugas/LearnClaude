package com.learnclaudeai.app.config;

import com.learnclaudeai.app.entity.Lesson;
import com.learnclaudeai.app.entity.User;
import com.learnclaudeai.app.repository.LessonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LessonSeeder implements CommandLineRunner {

    private final LessonRepository lessonRepository;

    public LessonSeeder(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    public void run(String... args) {
        if (lessonRepository.existsByNumber(1)) {
            return; // already seeded
        }

        // ========================================================
        // TIER 1 — FREE (features available on free Claude)
        // Users can practice every challenge without paying for Claude
        // ========================================================

        seed(1, "Claude.ai Chat", "claude-chat-basics", 1, User.Plan.FREE,
                "Learn the fundamentals of chatting with Claude.ai. Understand the interface, conversation flow, and how Claude processes your messages. Everything here works on free Claude — no upgrade needed.",
                "Claude AI chat tutorial beginner",
                "Start a conversation with Claude and ask it to explain a topic you're curious about. Try asking a follow-up question to see how it maintains context.");

        seed(2, "Writing Great Prompts", "writing-great-prompts", 1, User.Plan.FREE,
                "Master the art of writing effective prompts. Learn how to be specific, provide context, set the right tone, and get consistently better outputs from Claude — all skills you can practice right now for free.",
                "Claude AI prompt engineering tips beginner",
                "Write three different prompts asking Claude the same question: one vague, one specific, and one with a role + format instruction. Compare the outputs and notice what changed.");

        seed(3, "Files & Image Uploads", "files-image-uploads", 1, User.Plan.FREE,
                "Learn how to upload documents, images, and screenshots for Claude to read and analyze. Free Claude supports file uploads so you can practice extracting data, summarizing PDFs, and describing images.",
                "Claude AI upload files images tutorial",
                "Upload a PDF or screenshot to Claude and ask it to summarize the key points. Then upload a photo and ask Claude to describe what it sees in detail.");

        seed(4, "Artifacts & Markdown", "artifacts-markdown", 1, User.Plan.FREE,
                "Use Claude's Artifacts feature to generate standalone documents, code snippets, charts, and interactive visualizations. Learn markdown formatting to get beautifully structured outputs. Artifacts work on free Claude.",
                "Claude Artifacts markdown tutorial",
                "Ask Claude to create an Artifact — try a React component, an SVG diagram, or a formatted report. Edit the Artifact inline and see it update in real time.");

        seed(5, "Understanding Claude Plans", "claude-plans-free-vs-pro", 1, User.Plan.FREE,
                "Learn exactly what you get with free Claude vs Claude Pro. Understand usage limits, model access, and which Pro features are worth upgrading for. This lesson helps you decide if and when to go Pro.",
                "Claude Pro vs Free plan comparison 2025",
                "Visit claude.ai/settings and review your current plan details. Make a list of the top 3 Pro features you'd actually use in your daily workflow, and why.");

        // ========================================================
        // TIER 2 — PRO (features that require Claude Pro)
        // Users need a Claude Pro subscription to practice these
        // ========================================================

        seed(6, "Projects & System Prompts", "projects-system-prompts", 2, User.Plan.PRO,
                "Master Claude Projects — a Pro feature that lets you organize conversations with shared context and custom instructions. Learn to write system prompts that shape Claude's behavior for specific use cases.",
                "Claude Projects system prompts tutorial",
                "Create a new Project in Claude for a specific use case (e.g., 'Blog Writing Assistant'). Add a system prompt that defines the tone, audience, and format you want.");

        seed(7, "Memory & Personalization", "memory-personalization", 2, User.Plan.PRO,
                "Set up Claude's Memory feature to remember your preferences, role, writing style, and key facts across all conversations. Memory is a Pro feature that makes Claude feel like a true personal assistant.",
                "Claude AI memory personalization feature",
                "Enable Memory in Claude settings. Tell Claude your name, your job, and a specific preference (e.g., 'I prefer concise answers'). Start a new conversation and see if it remembers.");

        seed(8, "Web Search & Deep Research", "web-search-deep-research", 2, User.Plan.PRO,
                "Unlock Claude's ability to search the web for current information and perform Deep Research — multi-step investigations that synthesize dozens of sources into comprehensive reports. Both are Pro features.",
                "Claude web search deep research feature",
                "Ask Claude to do a Deep Research query on a topic relevant to your work. Compare the depth of the report versus a regular web search response.");

        seed(9, "Connectors & Integrations", "connectors-integrations", 2, User.Plan.PRO,
                "Connect Claude to your existing tools like Google Drive, Notion, and GitHub. Integrations are a Pro feature that let Claude access your real data for more personalized, context-aware answers.",
                "Claude AI connectors integrations setup",
                "Connect one integration (Google Drive or GitHub) to Claude. Ask Claude a question that requires it to reference a specific file or repo from your connected account.");

        seed(10, "Claude Mobile App", "claude-mobile-app", 2, User.Plan.PRO,
                "Get the most out of Claude on your phone. Learn mobile-specific features like voice conversations, camera uploads, and how to use Claude as a daily AI companion. The full experience requires Pro.",
                "Claude AI mobile app iOS Android tutorial",
                "Download the Claude app on your phone. Try a voice conversation and upload a photo of something around you for Claude to analyze.");

        seed(11, "Cowork (Desktop App)", "cowork-desktop-app", 2, User.Plan.PRO,
                "Use Claude's desktop app to work alongside you on your Mac or PC. Cowork mode — a Pro feature — lets Claude see your screen, suggest next steps, and automate repetitive tasks in real time.",
                "Claude Cowork desktop app tutorial",
                "Install the Claude desktop app and try Cowork mode. Let Claude watch your screen while you work and see what suggestions it offers.");

        // ========================================================
        // TIER 3 — PRO (developer & power user tools)
        // Advanced tools that extend Claude beyond the chat interface
        // ========================================================

        seed(12, "Claude in Chrome (Browser Agent)", "claude-in-chrome", 3, User.Plan.PRO,
                "Install the Claude browser extension and let it act as an intelligent agent inside Chrome. Claude can read web pages, fill forms, extract data, and automate browser workflows. Requires Claude Pro.",
                "Claude Chrome extension browser agent",
                "Install the Claude Chrome extension. Navigate to a web page and ask Claude to summarize it, extract key data points, or fill out a form for you.");

        seed(13, "Claude in Excel & PowerPoint", "claude-excel-powerpoint", 3, User.Plan.PRO,
                "Use Claude directly inside Microsoft Office apps. Generate formulas, analyze spreadsheet data, create PowerPoint presentations, and automate tedious Office tasks. Requires Claude Pro.",
                "Claude AI Excel PowerPoint integration tutorial",
                "Open Excel or Google Sheets and use Claude to write a complex formula. Then try generating a PowerPoint outline from a topic and see Claude build the slides.");

        seed(14, "Claude Code", "claude-code", 3, User.Plan.PRO,
                "Master Claude Code, the CLI-based coding agent. Write, debug, and refactor code directly in your terminal with full codebase awareness. Claude Code requires an Anthropic API key or Claude Pro.",
                "Claude Code CLI tutorial programming",
                "Install Claude Code and run it in a project directory. Ask it to add a new feature or fix a bug, and watch it read files, make edits, and run tests.");

        seed(15, "Claude API & Building Apps", "claude-api", 3, User.Plan.PRO,
                "Build applications powered by Claude using the Anthropic API. Learn authentication, message formatting, streaming responses, tool use, and best practices for production integrations.",
                "Claude API Anthropic tutorial getting started",
                "Get an API key from console.anthropic.com and make your first API call using curl or Python. Try sending a message with a system prompt and streaming the response.");

        seed(16, "Agents & Orchestration", "agents-orchestration", 3, User.Plan.PRO,
                "Build managed agents via the Anthropic Console, control Claude Code remotely, and orchestrate multi-agent workflows with Dispatch. Learn patterns for breaking down problems across coordinated agents.",
                "Claude managed agents dispatch orchestration tutorial",
                "Go to the Anthropic Console and create a managed agent with one tool. Then design a multi-agent workflow: one agent for research, one for writing, one for review.");

        seed(17, "AI-Powered Artifacts (Claude in Claude)", "ai-powered-artifacts", 3, User.Plan.PRO,
                "Create Artifacts that themselves use Claude's intelligence — interactive tools, analyzers, and mini-apps that call the AI from within. Build truly AI-native documents and experiences.",
                "Claude AI-powered Artifacts advanced tutorial",
                "Create an Artifact that includes an interactive element — like a quiz generator or text analyzer — that uses Claude's intelligence within the Artifact itself.");

        System.out.println("[LearnClaude.ai] Seeded 17 lessons successfully.");
    }

    private void seed(int number, String title, String slug, int tier, User.Plan plan,
                      String description, String youtubeSearchQuery, String challenge) {
        Lesson lesson = new Lesson();
        lesson.setNumber(number);
        lesson.setTitle(title);
        lesson.setSlug(slug);
        lesson.setTier(tier);
        lesson.setPlan(plan);
        lesson.setDescription(description);
        lesson.setYoutubeSearchQuery(youtubeSearchQuery);
        lesson.setChallenge(challenge);
        lessonRepository.save(lesson);
    }
}
