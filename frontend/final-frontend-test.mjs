import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/';
  
  console.log('📝 登录...');
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
  
  console.log('👥 进入客户管理...');
  const menu = await page.$('text=客户管理');
  if (menu) {
    await menu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(1500);
  }
  
  console.log('➕ 新增客户...');
  const addBtn = await page.$('button:has-text("新增")');
  if (addBtn) {
    await addBtn.click();
    await page.waitForTimeout(500);
  }
  
  console.log('✍️ 填写表单（所有字段）...');
  // 客户名称
  await page.fill('input[placeholder="请输入客户名称"]', '陈静');
  // 联系人
  await page.fill('input[placeholder="请输入联系人姓名"]', '陈静');
  // 电话
  await page.fill('input[placeholder="请输入联系电话"]', '13600136000');
  // 邮箱
  await page.fill('input[placeholder="请输入邮箱"]', 'chenjing@example.com');
  
  // 选择客户级别
  const levelSelect = await page.$('select');
  if (levelSelect) {
    await levelSelect.selectOption('B');
    console.log('   ✅ 级别：B 类客户');
  }
  
  // 选择来源
  const selects = await page.$$('select');
  if (selects.length > 1) {
    await selects[1].selectOption('online');
    console.log('   ✅ 来源：线上咨询');
  }
  
  console.log('💾 提交...');
  const submitBtn = await page.$('button:has-text("确定"), button:has-text("保存")');
  if (submitBtn) {
    await submitBtn.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(3000);
  }
  
  console.log('📸 截图...');
  await page.screenshot({ path: ss + 'frontend-chenjing-success.png', fullPage: true });
  
  const content = await page.content();
  if (content.includes('陈静')) {
    console.log('✅ 陈静已添加！前后端打通！');
  } else {
    console.log('⚠️ 未找到陈静');
  }
  
  await browser.close();
})();
