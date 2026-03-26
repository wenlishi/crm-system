const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const context = await browser.newContext({ viewport: { width: 1920, height: 1080 } });
  const page = await context.newPage();

  try {
    console.log('🦐 登录系统...');
    await page.goto('http://localhost:5173/login', { waitUntil: 'networkidle' });
    await page.fill('input[placeholder*="用户名"]', 'admin');
    await page.fill('input[type="password"]', 'admin123');
    await page.click('button:has-text("登录")');
    await page.waitForURL('**/dashboard', { timeout: 10000 });
    await new Promise(r => setTimeout(r, 2000));

    console.log('📸 截图 Dashboard...');
    await page.screenshot({ path: '/home/ubuntu/.openclaw/workspace/screenshots/crm_final_dashboard.png', fullPage: true });

    console.log('👥 访问客户列表...');
    await page.goto('http://localhost:5173/customers', { waitUntil: 'networkidle' });
    await new Promise(r => setTimeout(r, 2000));

    console.log('📸 截图客户列表...');
    await page.screenshot({ path: '/home/ubuntu/.openclaw/workspace/screenshots/crm_final_customers.png', fullPage: true });

    console.log('✅ 完成！');
  } catch (e) {
    console.error('❌ 错误:', e.message);
  } finally {
    await browser.close();
  }
})();
