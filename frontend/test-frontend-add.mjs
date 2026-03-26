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
    await page.waitForTimeout(1000);
  }
  
  console.log('✍️ 4. 检查并填写所有字段...');
  
  // 获取所有输入框
  const allInputs = await page.$$('input[type="text"], input:not([type]), input[type="number"], textarea');
  console.log(`   找到 ${allInputs.length} 个输入框`);
  
  // 填写所有必填字段
  console.log('\n   填写字段...');
  
  // 客户名称（必填）
  await page.fill('input[placeholder="请输入客户名称"]', '王强');
  console.log('   ✅ 客户名称：王强');
  
  // 联系电话
  await page.fill('input[placeholder="请输入联系电话"]', '13800138002');
  console.log('   ✅ 联系电话：13800138002');
  
  // 邮箱
  await page.fill('input[placeholder="请输入邮箱"]', 'wangqiang@test.com');
  console.log('   ✅ 邮箱：wangqiang@test.com');
  
  // 联系人姓名
  await page.fill('input[placeholder="请输入联系人姓名"]', '王强');
  console.log('   ✅ 联系人姓名：王强');
  
  // 公司名称
  const companyInput = await page.$('input[placeholder*="公司"]');
  if (companyInput) {
    await companyInput.fill('王强贸易公司');
    console.log('   ✅ 公司名称：王强贸易公司');
  }
  
  // 选择所有下拉框
  const selects = await page.$$('select');
  console.log(`\n   找到 ${selects.length} 个下拉框`);
  for (let i = 0; i < selects.length; i++) {
    try {
      await selects[i].selectOption('1');
      console.log(`   ✅ 下拉框 ${i+1} 已选择`);
    } catch(e) {}
  }
  
  console.log('\n💾 5. 提交保存...');
  const submitBtn = await page.$('button:has-text("确定"), button:has-text("保存")');
  if (submitBtn) {
    await submitBtn.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(3000);
  }
  
  console.log('📸 6. 截取结果...');
  await page.screenshot({ path: ss + 'frontend-wangqiang-result.png', fullPage: true });
  
  // 验证
  const content = await page.content();
  if (content.includes('王强')) {
    console.log('\n✅ 王强已成功添加！前后端已打通！');
  } else {
    console.log('\n❌ 未找到王强');
  }
  
  await browser.close();
  console.log('\n🎉 测试完成！');
})();
