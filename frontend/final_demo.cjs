const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const context = await browser.newContext({ viewport: { width: 1920, height: 1080 } });
  const page = await context.newPage();

  console.log('🦐 登录系统...');
  await page.goto('http://localhost:5173/login', { waitUntil: 'networkidle' });
  await page.fill('input[placeholder*="用户名"]', 'admin');
  await page.fill('input[type="password"]', 'admin123');
  await page.click('button:has-text("登录")');
  await page.waitForURL('**/dashboard', { timeout: 15000 });
  await new Promise(r => setTimeout(r, 3000));

  console.log('📸 Dashboard 截图...');
  await page.screenshot({ path: '/home/ubuntu/.openclaw/workspace/screenshots/crm_demo_dashboard.png', fullPage: true });

  console.log('👥 客户列表截图...');
  await page.goto('http://localhost:5173/customers', { waitUntil: 'networkidle' });
  await new Promise(r => setTimeout(r, 2000));
  await page.screenshot({ path: '/home/ubuntu/.openclaw/workspace/screenshots/crm_demo_customers.png', fullPage: true });

  console.log('➕ 新增客户...');
  await page.click('button:has-text("新增")');
  await new Promise(r => setTimeout(r, 1500));

  const ts = Date.now().toString().slice(-4);
  await page.fill('input[placeholder*="客户名称"]', `演示客户${ts}`);
  await page.fill('input[placeholder*="联系人"]', `演示联系人${ts}`);
  await page.fill('input[placeholder*="联系电话"]', `1380013${Math.floor(Math.random()*9000+1000)}`);
  
  console.log('💾 提交...');
  await page.click('button:has-text("确定")');
  await new Promise(r => setTimeout(r, 3000));

  console.log('📸 客户列表（添加后）...');
  await page.screenshot({ path: '/home/ubuntu/.openclaw/workspace/screenshots/crm_demo_after_add.png', fullPage: true });

  console.log('✅ 完成！');
  await browser.close();
})();
