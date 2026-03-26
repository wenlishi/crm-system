const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ 
    headless: true,
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });
  const context = await browser.newContext({
    viewport: { width: 1920, height: 1080 }
  });
  const page = await context.newPage();

  try {
    console.log('🦐 正在访问登录页...');
    await page.goto('http://localhost:5173/login', { waitUntil: 'networkidle', timeout: 15000 });
    await page.waitForSelector('input[placeholder*="用户名"]', { timeout: 10000 });
    console.log('✅ 登录页加载完成');

    console.log('📝 填写登录信息...');
    await page.fill('input[placeholder*="用户名"]', 'admin');
    await page.fill('input[type="password"]', 'admin123');
    await page.click('button:has-text("登录")');
    
    console.log('⏳ 等待跳转...');
    await page.waitForURL('**/dashboard', { timeout: 15000 });
    await new Promise(r => setTimeout(r, 3000));
    console.log('✅ 登录成功');

    await page.screenshot({ 
      path: '/home/ubuntu/.openclaw/workspace/screenshots/crm_dashboard.png',
      fullPage: true 
    });
    console.log('📸 Dashboard 截图完成');

    console.log('👥 访问客户管理页面...');
    await page.goto('http://localhost:5173/customers', { waitUntil: 'networkidle', timeout: 15000 });
    await new Promise(r => setTimeout(r, 2000));

    console.log('➕ 点击新增按钮...');
    await page.click('button:has-text("新增"), button:has-text("添加客户"), .ant-btn-primary');
    await new Promise(r => setTimeout(r, 2000));

    // 填写表单
    console.log('📝 填写客户信息...');
    const timestamp = Date.now();
    const customerName = `示例客户${timestamp.toString().slice(-4)}`;
    const contactName = `联系人${timestamp.toString().slice(-4)}`;
    const contactPhone = `1380013${Math.floor(Math.random() * 9000 + 1000)}`;

    // 客户名称（必填）
    await page.fill('input[placeholder*="客户名称"]', customerName);
    console.log(`✅ 客户名称：${customerName}`);

    // 联系人（必填）
    await page.fill('input[placeholder*="联系人"]', contactName);
    console.log(`✅ 联系人：${contactName}`);

    // 联系电话（必填）
    await page.fill('input[placeholder*="联系电话"]', contactPhone);
    console.log(`✅ 联系电话：${contactPhone}`);

    // 客户级别默认就是 B 类，不需要改

    // 提交
    console.log('💾 提交保存...');
    await page.click('button:has-text("确定"), button:has-text("保存")');
    await new Promise(r => setTimeout(r, 3000));

    // 截图客户列表
    console.log('📸 截图客户列表...');
    await page.screenshot({ 
      path: '/home/ubuntu/.openclaw/workspace/screenshots/crm_customer_list.png',
      fullPage: true 
    });

    console.log(`🔗 最终 URL: ${page.url()}`);
    console.log('✅ 完成！截图已保存到:');
    console.log('   /home/ubuntu/.openclaw/workspace/screenshots/crm_dashboard.png');
    console.log('   /home/ubuntu/.openclaw/workspace/screenshots/crm_customer_list.png');
    
  } catch (error) {
    console.error('❌ 错误:', error.message);
    try {
      await page.screenshot({ 
        path: '/home/ubuntu/.openclaw/workspace/screenshots/crm_error.png',
        fullPage: true 
      });
      console.log('📸 错误截图已保存');
    } catch (e) {}
  } finally {
    await browser.close();
  }
})();
