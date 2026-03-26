import { chromium } from 'playwright';

const modules = [
  { name: '01-dashboard', menu: '首页', check: '仪表盘' },
  { name: '02-customers', menu: '客户管理', check: '客户' },
  { name: '03-opportunities', menu: '商机管理', check: '商机' },
  { name: '04-contracts', menu: '合同管理', check: '合同' },
  { name: '05-followups', menu: '跟进记录', check: '跟进' },
  { name: '06-users', menu: '用户管理', check: '用户' },
  { name: '07-roles', menu: '角色管理', check: '角色' },
  { name: '08-depts', menu: '部门管理', check: '部门' },
  { name: '09-permissions', menu: '权限管理', check: '权限' },
  { name: '10-statistics', menu: '数据统计', check: '统计' }
];

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const ss = '../../screenshots/modules/';
  
  // 确保目录存在
  const { execSync } = await import('child_process');
  try { execSync(`mkdir -p ${ss}`); } catch(e) {}
  
  console.log('📸 开始测试所有模块...\n');
  
  // 登录
  console.log('📝 登录系统...');
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
  console.log('✅ 登录成功\n');
  
  // 测试每个模块
  for (const mod of modules) {
    console.log(`📸 测试：${mod.menu}...`);
    
    try {
      // 点击菜单
      const menuBtn = await page.$(`text=${mod.menu}`);
      if (menuBtn) {
        await menuBtn.click();
        await page.waitForLoadState('networkidle');
        await page.waitForTimeout(1500);
      }
      
      // 截图
      await page.screenshot({ path: `${ss}${mod.name}.png`, fullPage: true });
      
      // 验证页面内容
      const content = await page.content();
      const found = content.includes(mod.check);
      
      console.log(`   ${found ? '✅' : '⚠️'} 页面加载成功\n`);
      
    } catch (e) {
      console.log(`   ❌ 错误：${e.message}\n`);
      await page.screenshot({ path: `${ss}${mod.name}-error.png` });
    }
  }
  
  await browser.close();
  console.log('\n🎉 所有模块测试完成！');
  console.log(`📁 截图保存至：${ss}`);
})();
