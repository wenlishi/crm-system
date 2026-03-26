const { chromium } = require('playwright');
(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  await page.goto('http://localhost:3000', { waitUntil: 'networkidle' });
  await page.screenshot({ path: '../../screenshots/01-login.png', fullPage: true });
  console.log('Login page captured!');
  await browser.close();
})();
