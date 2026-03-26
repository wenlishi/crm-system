import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  console.log('📊 登录系统...');
  await page.goto('http://localhost:3000', { waitUntil: 'networkidle' });
  
  // 登录
  const inputs = await page.$$('input[type="text"]');
  if (inputs.length > 0) await inputs[0].fill('admin');
  const pwdInput = await page.$('input[type="password"]');
  if (pwdInput) await pwdInput.fill('admin123');
  const loginBtn = await page.$('button:has-text("登录")');
  if (loginBtn) {
    await loginBtn.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  
  console.log('📈 进入数据看板...');
  // 点击首页/仪表盘
  const dashboardMenu = await page.$('text=首页, text=仪表盘, text=数据看板');
  if (dashboardMenu) {
    await dashboardMenu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  
  console.log('📸 截取数据看板...');
  await page.screenshot({ 
    path: '../../screenshots/dashboard-final.png', 
    fullPage: true,
    type: 'png'
  });
  
  // 检查页面内容
  const content = await page.content();
  const hasStats = content.includes('统计') || content.includes('数据') || content.includes('Dashboard');
  console.log(hasStats ? '✅ 数据看板已捕获' : '⚠️ 页面内容待确认');
  
  await browser.close();
  console.log('完成！');
})();
