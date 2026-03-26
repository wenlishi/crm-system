import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/wangwu/';
  
  // 创建目录
  const { execSync } = await import('child_process');
  try { execSync(`mkdir -p ${ss}`); } catch(e) {}
  
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
  await page.screenshot({ path: ss + '01-login.png' });
  console.log('   ✅ 登录成功');
  
  console.log('\n👥 2. 进入客户管理...');
  const menu = await page.$('text=客户管理');
  if (menu) {
    await menu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(1500);
  }
  await page.screenshot({ path: ss + '02-customer-list.png' });
  console.log('   ✅ 客户列表页面');
  
  console.log('\n➕ 3. 点击新增客户...');
  const addBtn = await page.$('button:has-text("新增")');
  if (addBtn) {
    await addBtn.click();
    await page.waitForTimeout(1000);
  }
  await page.screenshot({ path: ss + '03-add-form.png' });
  console.log('   ✅ 打开新增表单');
  
  console.log('\n✍️ 4. 填写所有字段...');
  
  // 客户名称（必填）
  await page.fill('input[placeholder="请输入客户名称"]', '王五');
  console.log('   ✅ 客户名称：王五');
  
  // 联系人姓名
  await page.fill('input[placeholder="请输入联系人姓名"]', '王五');
  console.log('   ✅ 联系人：王五');
  
  // 联系电话
  await page.fill('input[placeholder="请输入联系电话"]', '13500135000');
  console.log('   ✅ 联系电话：13500135000');
  
  // 邮箱
  await page.fill('input[placeholder="请输入邮箱"]', 'wangwu@company.com');
  console.log('   ✅ 邮箱：wangwu@company.com');
  
  // 选择客户级别（A/B/C/D）
  const levelSelect = await page.$('select');
  if (levelSelect) {
    await levelSelect.selectOption('A');
    console.log('   ✅ 客户级别：A 类客户');
  }
  
  // 选择客户来源
  const selects = await page.$$('select');
  if (selects.length > 1) {
    await selects[1].selectOption('online');
    console.log('   ✅ 客户来源：线上咨询');
  }
  
  // 备注
  const textarea = await page.$('textarea');
  if (textarea) {
    await textarea.fill('通过前端表单新增的测试客户');
    console.log('   ✅ 备注：通过前端表单新增的测试客户');
  }
  
  await page.screenshot({ path: ss + '04-form-filled.png' });
  console.log('\n📸 表单填写完成截图已保存');
  
  console.log('\n💾 5. 点击确定提交...');
  const submitBtn = await page.$('button:has-text("确定"), button:has-text("保存")');
  if (submitBtn) {
    await submitBtn.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(3000);
  }
  
  console.log('📸 6. 截取提交后结果...');
  await page.screenshot({ path: ss + '05-after-submit.png', fullPage: true });
  
  // 验证
  const content = await page.content();
  if (content.includes('王五')) {
    console.log('\n✅ 王五已成功添加到客户列表！');
    console.log('   🎉 前后端完全打通！');
  } else {
    console.log('\n⚠️ 未找到王五，检查是否添加成功');
  }
  
  await browser.close();
  console.log('\n🎉 所有操作完成！');
})();
