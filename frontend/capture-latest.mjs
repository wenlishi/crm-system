import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/latest/';
  
  // 创建目录
  const { execSync } = await import('child_process');
  try { execSync(`mkdir -p ${ss}`); } catch(e) {}
  
  console.log('📝 登录系统...');
  await page.goto('http://localhost:3000', { waitUntil: 'networkidle' });
  
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
  
  console.log('📊 1. 截取数据看板...');
  const dashboardMenu = await page.$('text=首页, text=仪表盘');
  if (dashboardMenu) {
    await dashboardMenu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  await page.screenshot({ path: ss + '01-dashboard.png', fullPage: true });
  console.log('   ✅ 数据看板已截图');
  
  console.log('👥 2. 截取客户管理...');
  const customerMenu = await page.$('text=客户管理');
  if (customerMenu) {
    await customerMenu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  await page.screenshot({ path: ss + '02-customers.png', fullPage: true });
  console.log('   ✅ 客户管理已截图');
  
  // 验证客户列表中有数据
  const content = await page.content();
  const customers = ['张三', '李明', '测试顾客'];
  const found = customers.filter(c => content.includes(c));
  console.log(`   📋 客户列表包含：${found.join(', ') || '待确认'}`);
  
  await browser.close();
  console.log('\n🎉 完成！');
})();
