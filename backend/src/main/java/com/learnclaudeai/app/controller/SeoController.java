package com.learnclaudeai.app.controller;

import com.learnclaudeai.app.entity.Lesson;
import com.learnclaudeai.app.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SeoController {

    private final LessonRepository lessonRepository;
    private final String siteUrl;

    public SeoController(LessonRepository lessonRepository,
                         @Value("${site.url:https://learnclaudeai.com}") String siteUrl) {
        this.lessonRepository = lessonRepository;
        this.siteUrl = siteUrl;
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        List<Lesson> lessons = lessonRepository.findAllByOrderByNumberAsc();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // Static pages
        addUrl(xml, siteUrl + "/", "1.0", "weekly");
        addUrl(xml, siteUrl + "/learn", "0.9", "weekly");
        addUrl(xml, siteUrl + "/register", "0.7", "monthly");
        addUrl(xml, siteUrl + "/login", "0.5", "monthly");

        // Lesson SEO pages
        for (Lesson lesson : lessons) {
            addUrl(xml, siteUrl + "/learn/" + lesson.getSlug(), "0.8", "weekly");
        }

        xml.append("</urlset>");
        return xml.toString();
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String robots() {
        return """
                User-agent: *
                Allow: /
                Allow: /learn
                Allow: /learn/
                Disallow: /dashboard
                Disallow: /admin
                Disallow: /lessons/
                Disallow: /api/

                Sitemap: %s/sitemap.xml
                """.formatted(siteUrl);
    }

    private void addUrl(StringBuilder xml, String loc, String priority, String changefreq) {
        xml.append("  <url>\n");
        xml.append("    <loc>").append(loc).append("</loc>\n");
        xml.append("    <priority>").append(priority).append("</priority>\n");
        xml.append("    <changefreq>").append(changefreq).append("</changefreq>\n");
        xml.append("  </url>\n");
    }
}
