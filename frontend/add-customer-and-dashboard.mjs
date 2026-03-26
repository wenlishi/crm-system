import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/';
  
  console.log('📝 1. 登录系统...');
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
  
  console.log('👥 2. 进入客户管理...');
  const customerMenu = await page.$('text=客户管理');
  if (customerMenu) {
    await customerMenu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(1500);
  }
  
  console.log('➕ 3. 点击新增客户...');
  const addBtn = await page.$('button:has-text("新增")');
  if (addBtn) {
    await addBtn.click();
    await page.waitForTimeout(500);
  }
  
  console.log('✍️ 4. 填写客户信息...');
  // 填写表单
  await page.fill('input[placeholder="请输入客户名称"]', '李明');
  await page.fill('input[placeholder="请输入联系电话"]', '13912345678');
  await page.fill('input[placeholder="请输入邮箱"]', 'liming@company.com');
  await page.fill('input[placeholder="请输入联系人姓名"]', '李明');
  
  // 选择客户类型和来源（如果有下拉框）
  const selects = await page.$$('select');
  if (selects.length > 0) {
    await selects[0].selectOption('1'); // 客户类型
  }
  
  console.log('💾 5. 提交保存...');
  const submitBtn = await page.$('button:has-text("确定"), button:has-text("保存")');
  if (submitBtn) {
    await submitBtn.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  
  console.log('📸 6. 截取客户添加成功画面...');
  await page.screenshot({ path: ss + 'customer-add-success.png', fullPage: true });
  
  // 验证是否成功
  const content = await page.content();
  if (content.includes('李明') || content.includes('成功')) {
    console.log('✅ 客户添加成功！');
  }
  
  console.log('📊 7. 进入数据看板...');
  const dashboardMenu = await page.$('text=首页, text=仪表盘');
  if (dashboardMenu) {
    await dashboardMenu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  
  console.log('📸 8. 截取数据看板...');
  await page.screenshot({ path: ss + 'dashboard-after-add.png', fullPage: true });
  console.log('✅ 数据看板已捕获！');
  
  await browser.close();
  console.log('\n🎉 完成所有操作！');
})();
