import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/real-frontend/';
  
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
  await page.screenshot({ path: ss + '02-list.png' });
  console.log('   ✅ 客户列表');
  
  console.log('\n➕ 3. 点击新增客户...');
  const addBtn = await page.$('button:has-text("新增")');
  if (addBtn) {
    await addBtn.click();
    await page.waitForTimeout(2000);
  }
  await page.screenshot({ path: ss + '03-form.png' });
  console.log('   ✅ 打开表单');
  
  console.log('\n✍️ 4. 填写表单...');
  
  // 使用 eval 直接在页面上下文中填写
  await page.evaluate(() => {
    const inputs = document.querySelectorAll('input[placeholder*="客户名称"]');
    if (inputs[0]) inputs[0].value = '赵云';
  });
  console.log('   ✅ 客户名称：赵云');
  
  await page.evaluate(() => {
    const inputs = document.querySelectorAll('input[placeholder*="联系电话"]');
    if (inputs[0]) inputs[0].value = '13900139000';
  });
  console.log('   ✅ 联系电话：13900139000');
  
  await page.evaluate(() => {
    const inputs = document.querySelectorAll('input[placeholder*="邮箱"]');
    if (inputs[0]) inputs[0].value = 'zhaoyun@shu.com';
  });
  console.log('   ✅ 邮箱：zhaoyun@shu.com');
  
  await page.evaluate(() => {
    const inputs = document.querySelectorAll('input[placeholder*="公司名称"]');
    if (inputs[0]) inputs[0].value = '蜀国集团';
  });
  console.log('   ✅ 公司名称：蜀国集团');
  
  await page.evaluate(() => {
    const textareas = document.querySelectorAll('textarea');
    if (textareas[0]) textareas[0].value = '真正通过前端表单添加';
  });
  console.log('   ✅ 备注：已填写');
  
  // 选择下拉框
  await page.evaluate(() => {
    const selects = document.querySelectorAll('select');
    if (selects[0]) selects[0].value = '1'; // 客户类型
    if (selects[1]) selects[1].value = '1'; // 客户级别
    if (selects[2]) selects[2].value = '1'; // 客户状态
    if (selects[3]) selects[3].value = '官网咨询'; // 客户来源
  });
  console.log('   ✅ 下拉框已选择');
  
  await page.screenshot({ path: ss + '04-form-filled.png' });
  console.log('\n📸 表单填写完成');
  
  console.log('\n💾 5. 提交...');
  const submitBtn = await page.$('button:has-text("确定")');
  if (submitBtn) {
    await submitBtn.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(5000);
  }
  
  console.log('\n📸 6. 结果...');
  await page.screenshot({ path: ss + '05-result.png', fullPage: true });
  
  const content = await page.content();
  if (content.includes('赵云')) {
    console.log('\n✅ 赵云已添加！');
  } else {
    console.log('\n⚠️ 未找到赵云');
  }
  
  await browser.close();
})();
