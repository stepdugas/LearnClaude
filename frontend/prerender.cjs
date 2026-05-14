/**
 * Pre-render script for SEO pages.
 * Generates static HTML snapshots for /learn and /learn/{slug} routes
 * so search engines see real content instead of a blank SPA shell.
 *
 * Run after `npm run build`:
 *   node prerender.js
 *
 * Requires the backend API to be running (for lesson data).
 */

const fs = require('fs')
const path = require('path')

const API_URL = process.env.VITE_API_URL || 'http://localhost:8080'
const DIST = path.resolve(__dirname, 'dist')
const SITE_URL = process.env.SITE_URL || 'https://learnclaudeai.com'

async function prerender() {
  console.log('[prerender] Fetching lessons from API...')

  let lessons
  try {
    const res = await fetch(`${API_URL}/api/public/lessons`)
    lessons = await res.json()
  } catch (e) {
    console.error('[prerender] Could not reach API. Is the backend running?')
    console.error('[prerender] Skipping pre-render. SEO pages will render client-side.')
    return
  }

  console.log(`[prerender] Found ${lessons.length} lessons`)

  const indexHtml = fs.readFileSync(path.join(DIST, 'index.html'), 'utf-8')

  // Generate /learn/index.html
  const learnDir = path.join(DIST, 'learn')
  fs.mkdirSync(learnDir, { recursive: true })

  const learnIndexMeta = buildMeta(
    'Learn How to Use Claude AI — Interactive Lessons | LearnClaude.ai',
    '17 hands-on lessons teaching every Claude AI tool. Free lessons available. Powered by AI tutoring.',
    `${SITE_URL}/learn`
  )
  fs.writeFileSync(
    path.join(learnDir, 'index.html'),
    injectMeta(indexHtml, learnIndexMeta)
  )
  console.log('[prerender] /learn/index.html')

  // Generate /learn/{slug}/index.html for each lesson
  for (const lesson of lessons) {
    const slugDir = path.join(learnDir, lesson.slug)
    fs.mkdirSync(slugDir, { recursive: true })

    const title = `${lesson.title} — Learn Claude AI | LearnClaude.ai`
    const desc = `Learn how to use ${lesson.title} in Claude AI. Interactive lesson with AI tutor and hands-on challenge.`

    const meta = buildMeta(title, desc.slice(0, 155), `${SITE_URL}/learn/${lesson.slug}`)
    fs.writeFileSync(
      path.join(slugDir, 'index.html'),
      injectMeta(indexHtml, meta)
    )
    console.log(`[prerender] /learn/${lesson.slug}/index.html`)
  }

  console.log(`[prerender] Done. ${lessons.length + 1} pages pre-rendered.`)
}

function buildMeta(title, description, canonical) {
  return { title, description, canonical }
}

function injectMeta(html, meta) {
  return html
    .replace(
      /<title>.*?<\/title>/,
      `<title>${meta.title}</title>`
    )
    .replace(
      /<meta name="description".*?\/>/,
      `<meta name="description" content="${meta.description}" />`
    )
    .replace(
      '</head>',
      `  <link rel="canonical" href="${meta.canonical}" />
    <meta property="og:title" content="${meta.title}" />
    <meta property="og:description" content="${meta.description}" />
    <meta property="og:url" content="${meta.canonical}" />
    <meta property="og:type" content="website" />
  </head>`
    )
}

prerender().catch(console.error)
