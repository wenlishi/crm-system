const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const context = await browser.newContext({
    viewport: { width: 1920, height: 1080 }
  });
  const page = await context.newPage();

  console.log('🦐 正在访问登录页...');
  await page.goto('http://localhost:5173/login', { waitUntil: 'networkidle' });
  
  // 等待登录页加载
  await page.waitForSelector('input[placeholder*="用户名"]', { timeout: 10000 });
  console.log('✅ 登录页加载完成');

  // 填写登录表单
  console.log('📝 填写登录信息...');
  await page.fill('input[placeholder*="用户名"]', 'admin');
  await page.fill('input[type="password"]', 'admin123');
  
  // 点击登录
  console.log('🔐 点击登录...');
  await page.click('button:has-text("登录")');
  
  // 等待路由跳转到 dashboard
  console.log('⏳ 等待跳转...');
  await page.waitForURL('**/dashboard', { timeout: 10000 });
  
  // 等待关键元素渲染
  await page.waitForSelector('.echarts, .ant-card', { timeout: 5000 });
  await new Promise(r => setTimeout(r, 2000));
  console.log('✅ 登录成功，进入 Dashboard');

  // 截图 Dashboard
  await page.screenshot({ 
    path: '/home/ubuntu/.openclaw/workspace/screenshots/crm_dashboard.png',
    fullPage: true 
  });
  console.log('📸 Dashboard 截图完成');

  // 导航到客户管理页面
  console.log('👥 导航到客户管理...');
  
  // 尝试多种可能的菜单选择器
  try {
    await page.click('.ant-menu-title-content:has-text("客户管理"), .ant-menu-item:has-text("客户"), .ant-menu:has-text("客户管理")');
    await new Promise(r => setTimeout(r, 2000));
  } catch (e) {
    console.log('菜单点击失败，尝试其他方式...');
    // 可能需要在侧边栏找
    await page.click('span:has-text("客户"), div:has-text("客户管理")').catch(() => {});
    await new Promise(r => setTimeout(r, 1500));
  }
  
  // 查找添加按钮
  console.log('➕ 查找添加按钮...');
  try {
    await page.click('button:has-text("添加客户"), button:has-text("新建客户"), button:has-text("新增"), .ant-btn-primary:has-text("+"), .ant-btn:has-text("添加")');
    await new Promise(r => setTimeout(r, 1500));
  } catch (e) {
    console.log('未找到添加按钮，可能已在列表页');
  }

  // 填写客户信息
  console.log('📝 填写示例客户信息...');
  const timestamp = Date.now();
  const customerName = `示例客户${timestamp.toString().slice(-4)}`;
  const customerPhone = `1380013${Math.floor(Math.random() * 9000 + 1000)}`;
  
  // 查找所有输入框
  const inputs = page.locator('input[type="text"], input:not([type="password"]):not([type="checkbox"])');
  const count = await inputs.count();
  console.log(`找到 ${count} 个输入框`);
  
  if (count >= 1) {
    await inputs.nth(0).fill(customerName);
    console.log(`✅ 填写姓名：${customerName}`);
  }
  if (count >= 2) {
    await inputs.nth(1).fill(customerPhone);
    console.log(`✅ 填写电话：${customerPhone}`);
  }
  if (count >= 3) {
    await inputs.nth(2).fill('测试科技公司');
    console.log('✅ 填写公司');
  }

  // 提交
  console.log('💾 提交保存...');
  try {
    await page.click('button:has-text("确定"), button:has-text("保存"), .ant-btn-primary[type="button"], .ant-modal-footer button:nth-child(2)');
  } catch (e) {
    await page.click('.ant-btn-primary').catch(() => {});
  }
  
  await new Promise(r => setTimeout(r, 2500));

  // 截图客户列表
  console.log('📸 截图客户列表...');
  await page.screenshot({ 
    path: '/home/ubuntu/.openclaw/workspace/screenshots/crm_customer_list.png',
    fullPage: true 
  });

  const url = page.url();
  console.log(`🔗 最终 URL: ${url}`);

  await browser.close();
  console.log('✅ 完成！截图已保存到:');
  console.log('   /home/ubuntu/.openclaw/workspace/screenshots/crm_dashboard.png');
  console.log('   /home/ubuntu/.openclaw/workspace/screenshots/crm_customer_list.png');
})();
