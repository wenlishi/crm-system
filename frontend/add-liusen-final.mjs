import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/liusen/';
  
  console.log('登录...');
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
  
  console.log('进入客户管理...');
  const menu = await page.$('text=客户管理');
  if (menu) {
    await menu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(1500);
  }
  
  console.log('新增客户...');
  const addBtn = await page.$('button:has-text("新增")');
  if (addBtn) {
    await addBtn.click();
    await page.waitForTimeout(2000); // 等待表单完全加载
  }
  
  console.log('填写表单...');
  // 填写所有字段
  await page.fill('input[placeholder*="客户名称"]', '刘森');
  await page.fill('input[placeholder*="联系电话"]', '13612345678');
  await page.fill('input[placeholder*="邮箱"]', 'liusen@example.com');
  
  await page.waitForTimeout(1000);
  
  // 选择下拉框
  const allSelects = await page.$$('select');
  console.log(`找到 ${allSelects.length} 个下拉框`);
  
  if (allSelects.length >= 4) {
    await allSelects[0].selectOption('1'); // 客户类型
    await allSelects[1].selectOption('2'); // 客户级别
    await allSelects[2].selectOption('1'); // 客户状态
    await allSelects[3].selectOption('电话咨询'); // 客户来源
    console.log('✅ 所有下拉框已选择');
  } else {
    console.log('⚠️ 下拉框数量不足');
  }
  
  // 公司名称
  await page.fill('input[placeholder*="公司名称"]', '刘森贸易公司');
  
  // 备注
  const textarea = await page.$('textarea');
  if (textarea) {
    await textarea.fill('通过前端新增');
  }
  
  await page.screenshot({ path: ss + 'form-complete.png' });
  
  console.log('提交...');
  const submitBtn = await page.$('button:has-text("确定")');
  if (submitBtn) {
    await submitBtn.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(5000);
  }
  
  console.log('截图...');
  await page.screenshot({ path: ss + 'result.png', fullPage: true });
  
  const content = await page.content();
  if (content.includes('刘森')) {
    console.log('✅ 成功！');
  } else {
    console.log('⚠️ 未找到');
  }
  
  await browser.close();
})();
