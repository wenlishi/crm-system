import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: false });
  const page = await browser.newPage();
  const ss = '../../screenshots/';
  
  console.log('检查各模块报错...\n');
  
  // 登录
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
  
  // 检查部门管理（之前标记为⚠️）
  console.log('📸 检查部门管理...');
  const deptMenu = await page.$('text=部门管理');
  if (deptMenu) {
    await deptMenu.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }
  await page.screenshot({ path: ss + 'dept-check.png', fullPage: true });
  
  // 检查页面错误
  const errors = await page.evaluate(() => {
    const errs = [];
    document.querySelectorAll('.el-message--error, .ant-message-error, [class*="error"], [style*="red"]')
      .forEach(el => {
        const text = el.textContent?.trim();
        if (text && text.length < 200) errs.push(text);
      });
    return errs;
  });
  
  if (errors.length > 0) {
    console.log('❌ 发现错误：');
    errors.forEach(e => console.log('   -', e));
  } else {
    console.log('✅ 未发现明显错误');
  }
  
  // 检查控制台错误
  page.on('console', msg => {
    if (msg.type() === 'error') {
      console.log('🔴 Console Error:', msg.text());
    }
  });
  
  await browser.close();
})();
