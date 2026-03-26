import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/';
  
  try {
    // 1. 登录页
    console.log('1. Opening login page...');
    await page.goto('http://localhost:3000', { waitUntil: 'networkidle', timeout: 15000 });
    await page.screenshot({ path: ss + '01-login.png', fullPage: true });
    
    // 2. 登录
    console.log('2. Logging in as admin...');
    const inputs = await page.$$('input[type="text"]');
    if (inputs.length > 0) {
      await inputs[0].fill('admin');
    }
    const pwdInput = await page.$('input[type="password"]');
    if (pwdInput) {
      await pwdInput.fill('admin123');
    }
    
    const loginBtn = await page.$('button:has-text("登录"), button[type="submit"]');
    if (loginBtn) {
      await loginBtn.click();
      await page.waitForLoadState('networkidle', { timeout: 10000 });
      await page.waitForTimeout(2000);
    }
    await page.screenshot({ path: ss + '02-dashboard.png', fullPage: true });
    console.log('   Dashboard captured!');
    
    // 3. 进入客户管理
    console.log('3. Navigating to customers...');
    const customerLink = await page.$('text=客户管理, text=客户');
    if (customerLink) {
      await customerLink.click();
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(1000);
    }
    await page.screenshot({ path: ss + '03-customer-list.png', fullPage: true });
    
    // 4. 点击新增
    console.log('4. Clicking add new customer...');
    const addBtn = await page.$('button:has-text("新增"), button:has-text("添加客户")');
    if (addBtn) {
      await addBtn.click();
      await page.waitForTimeout(500);
    }
    await page.screenshot({ path: ss + '04-add-form.png', fullPage: true });
    
    // 5. 填写表单
    console.log('5. Filling customer form...');
    const textInputs = await page.$$('input[type="text"], input:not([type])');
    for (const input of textInputs) {
      const placeholder = await input.getAttribute('placeholder');
      if (placeholder && placeholder.includes('客户名称')) {
        await input.fill('张三');
      } else if (placeholder && placeholder.includes('手机')) {
        await input.fill('13800138000');
      } else if (placeholder && placeholder.includes('邮箱')) {
        await input.fill('zhangsan@example.com');
      } else if (placeholder && (placeholder.includes('公司') || placeholder.includes('单位'))) {
        await input.fill('示例科技公司');
      }
    }
    
    // 6. 提交
    console.log('6. Submitting form...');
    const submitBtn = await page.$('button:has-text("确定"), button:has-text("保存"), button:has-text("提交")');
    if (submitBtn) {
      await submitBtn.click();
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(1500);
    }
    await page.screenshot({ path: ss + '05-success.png', fullPage: true });
    
    // 7. 查看顾客列表
    console.log('7. Capturing customer list with new entry...');
    await page.waitForTimeout(1000);
    await page.screenshot({ path: ss + '06-customer-with-data.png', fullPage: true });
    
    console.log('\n✅ All screenshots captured successfully!');
    console.log('Screenshots saved to:', ss);
    
  } catch (e) {
    console.error('Error:', e.message);
    await page.screenshot({ path: ss + 'error.png', fullPage: true });
  } finally {
    await browser.close();
  }
})();
