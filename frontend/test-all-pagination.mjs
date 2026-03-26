import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/pagination-test/';
  
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
  console.log('   ✅ 登录成功');
  
  // 模块列表
  const modules = [
    { name: '01-customers', menu: '客户管理' },
    { name: '02-opportunities', menu: '商机管理' },
    { name: '03-contracts', menu: '合同管理' },
    { name: '04-followups', menu: '跟进记录' },
    { name: '05-users', menu: '用户管理' },
    { name: '06-roles', menu: '角色权限' }
  ];
  
  for (const mod of modules) {
    console.log(`\n📊 ${mod.name}: 测试${mod.menu}...`);
    
    // 点击菜单
    const menuBtn = await page.$(`text=${mod.menu}`);
    if (menuBtn) {
      await menuBtn.click();
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(2000);
    }
    
    // 截图
    await page.screenshot({ path: `${ss}${mod.name}.png`, fullPage: true });
    
    // 检查分页组件
    const hasPagination = await page.$('.el-pagination');
    if (hasPagination) {
      console.log(`   ✅ 分页组件已显示`);
    } else {
      console.log(`   ❌ 分页组件未显示`);
    }
  }
  
  await browser.close();
  console.log('\n🎉 所有模块测试完成！');
})();
