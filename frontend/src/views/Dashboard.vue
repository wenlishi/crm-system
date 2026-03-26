<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1 class="welcome-title">欢迎回来</h1>
          <p class="welcome-subtitle">继续完成今天的任务，一切进展顺利</p>
        </div>
        <div class="welcome-date">
          <el-icon><Calendar /></el-icon>
          <span>{{ currentDate }}</span>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <div class="stat-card customer-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <div class="stat-icon-bg"></div>
              <el-icon class="stat-icon"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">客户总数</div>
              <div class="stat-value">{{ formatNumber(stats.totalCustomers) }}</div>
              <div class="stat-trend positive" v-if="stats.todayCustomers > 0">
                <span class="trend-icon">↑</span>
                <span>今日新增 {{ stats.todayCustomers }} 人</span>
              </div>
            </div>
          </div>
          <div class="stat-card-footer"></div>
        </div>
      </el-col>
      
      <el-col :span="6">
        <div class="stat-card followup-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <div class="stat-icon-bg"></div>
              <el-icon class="stat-icon"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">跟进记录</div>
              <div class="stat-value">{{ formatNumber(stats.totalFollowUps) }}</div>
              <div class="stat-trend positive" v-if="stats.weekFollowUps > 0">
                <span class="trend-icon">↑</span>
                <span>本周 {{ stats.weekFollowUps }} 次</span>
              </div>
            </div>
          </div>
          <div class="stat-card-footer"></div>
        </div>
      </el-col>
      
      <el-col :span="6">
        <div class="stat-card opportunity-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <div class="stat-icon-bg"></div>
              <el-icon class="stat-icon"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">商机数量</div>
              <div class="stat-value">{{ formatNumber(stats.totalOpportunities) }}</div>
            </div>
          </div>
          <div class="stat-card-footer"></div>
        </div>
      </el-col>
      
      <el-col :span="6">
        <div class="stat-card contract-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <div class="stat-icon-bg"></div>
              <el-icon class="stat-icon"><DocumentCopy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">合同金额</div>
              <div class="stat-value">{{ formatMoney(stats.totalContractAmount) }}</div>
            </div>
          </div>
          <div class="stat-card-footer"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="charts-row">
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <span class="chart-title">客户增长趋势</span>
              <span class="chart-badge">实时更新</span>
            </div>
          </div>
          <div ref="customerGrowthChart" class="chart"></div>
        </div>
      </el-col>
      
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <span class="chart-title">客户级别分布</span>
              <span class="chart-badge warning">重要</span>
            </div>
          </div>
          <div ref="customerLevelChart" class="chart"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="charts-row">
      <el-col :span="24">
        <div class="chart-card chart-card-large">
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <span class="chart-title">合同金额统计</span>
              <span class="chart-badge primary">年度累计</span>
            </div>
          </div>
          <div ref="contractChart" class="chart-large"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { User, Document, Money, DocumentCopy, Calendar } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import axios from 'axios'

const stats = ref({
  totalCustomers: 0,
  totalFollowUps: 0,
  totalOpportunities: 0,
  totalContractAmount: 0,
  todayCustomers: 0,
  weekFollowUps: 0,
  validCustomers: 0
})

const currentDate = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const weekDay = weekDays[now.getDay()]
  return `${year}年${month}月${day}日 ${weekDay}`
})

const customerGrowthChart = ref(null)
const customerLevelChart = ref(null)
const contractChart = ref(null)

const getToken = () => localStorage.getItem('token')

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = getToken()
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

const formatMoney = (value) => {
  if (!value) return '0.00'
  const num = Number(value)
  if (num >= 100000000) {
    return (num / 100000000).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + '亿'
  } else if (num >= 10000) {
    return (num / 10000).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + '万'
  }
  return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const formatNumber = (value) => {
  if (!value) return '0'
  const num = Number(value)
  if (num >= 100000000) return (num / 100000000).toFixed(2) + '亿'
  if (num >= 10000) return (num / 10000).toFixed(2) + '万'
  return num.toLocaleString('zh-CN')
}

const loadStats = async () => {
  try {
    const dashboardRes = await request.get('/statistics/dashboard')
    if (dashboardRes.data.code === 200) {
      const data = dashboardRes.data.data
      stats.value.totalCustomers = Number(data.totalCustomers) || 0
      stats.value.totalFollowUps = Number(data.totalFollowUps) || 0
      stats.value.todayCustomers = Number(data.todayCustomers) || 0
      stats.value.weekFollowUps = Number(data.weekFollowUps) || 0
      stats.value.validCustomers = Number(data.validCustomers) || 0
    }
    
    try {
      const oppRes = await request.get('/opportunities/stats/stage')
      if (oppRes.data.code === 200) {
        stats.value.totalOpportunities = Number(oppRes.data.data.total) || 0
      }
    } catch (e) {
      try {
        const oppListRes = await request.get('/opportunities/page?current=1&size=1')
        if (oppListRes.data.code === 200) {
          stats.value.totalOpportunities = oppListRes.data.data.total || 0
        }
      } catch (e2) {}
    }
    
    try {
      const contractRes = await request.get('/contracts/page?current=1&size=100')
      if (contractRes.data.code === 200) {
        const contracts = contractRes.data.data.records || []
        stats.value.totalContractAmount = contracts.reduce((sum, c) => sum + (Number(c.amount) || 0), 0)
      }
    } catch (e) {}
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadCustomerGrowth = async () => {
  try {
    const res = await request.get('/statistics/customer-growth')
    if (res.data.code === 200 && res.data.data.dailyStats) {
      const dailyStats = res.data.data.dailyStats
      return {
        dates: dailyStats.map(item => item.date),
        counts: dailyStats.map(item => Number(item.count))
      }
    }
  } catch (error) {}
  return { dates: [], counts: [] }
}

const loadCustomerLevel = async () => {
  try {
    const res = await request.get('/customers/page?current=1&size=100')
    if (res.data.code === 200) {
      const customers = res.data.data.records || []
      const levelCount = { 1: 0, 2: 0, 3: 0 }
      customers.forEach(c => {
        const level = c.level || 1
        levelCount[level] = (levelCount[level] || 0) + 1
      })
      return [
        { value: levelCount[1] || 0, name: '普通客户' },
        { value: levelCount[2] || 0, name: 'VIP 客户' },
        { value: levelCount[3] || 0, name: '重要客户' }
      ]
    }
  } catch (error) {}
  return []
}

const loadContractStats = async () => {
  try {
    const res = await request.get('/contracts/page?current=1&size=100')
    if (res.data.code === 200) {
      const contracts = res.data.data.records || []
      const monthAmount = {}
      contracts.forEach(c => {
        if (c.signDate) {
          const month = c.signDate.substring(0, 7)
          monthAmount[month] = (monthAmount[month] || 0) + (Number(c.amount) || 0)
        }
      })
      return {
        months: Object.keys(monthAmount).sort(),
        amounts: Object.keys(monthAmount).sort().map(m => monthAmount[m])
      }
    }
  } catch (error) {}
  return { months: [], amounts: [] }
}

const initCharts = async () => {
  const growthData = await loadCustomerGrowth()
  const levelData = await loadCustomerLevel()
  const contractData = await loadContractStats()
  
  // 客户增长趋势图
  if (customerGrowthChart.value) {
    const chart = echarts.init(customerGrowthChart.value)
    chart.setOption({
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 1)',
        borderColor: '#e8e8e8',
        borderWidth: 1,
        textStyle: { color: '#333', fontSize: 13 },
        padding: [14, 18],
        extraCssText: 'box-shadow: 0 4px 12px rgba(0,0,0,0.08); border-radius: 8px;'
      },
      grid: { left: '3%', right: '4%', bottom: '3%', top: '8%', containLabel: true },
      xAxis: {
        type: 'category',
        data: growthData.dates.length > 0 ? growthData.dates : ['暂无数据'],
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: '#999', fontSize: 12, margin: 16 }
      },
      yAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: '#f5f5f5' } },
        axisLabel: { color: '#999', fontSize: 12 },
        axisTick: { show: false },
        axisLine: { show: false }
      },
      series: [{
        data: growthData.counts.length > 0 ? growthData.counts : [0],
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 10,
        itemStyle: {
          color: '#1890ff',
          borderWidth: 3,
          borderColor: '#fff',
          shadowColor: 'rgba(24, 144, 255, 0.3)',
          shadowBlur: 10
        },
        lineStyle: {
          color: '#1890ff',
          width: 3,
          shadowColor: 'rgba(24, 144, 255, 0.3)',
          shadowBlur: 10,
          shadowOffsetY: 4
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(24, 144, 255, 0.25)' },
              { offset: 1, color: 'rgba(24, 144, 255, 0.02)' }
            ]
          }
        }
      }]
    })
  }

  // 客户级别分布图
  if (customerLevelChart.value) {
    const chart = echarts.init(customerLevelChart.value)
    chart.setOption({
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(255, 255, 255, 1)',
        borderColor: '#e8e8e8',
        borderWidth: 1,
        textStyle: { color: '#333', fontSize: 13 },
        padding: [14, 18],
        extraCssText: 'box-shadow: 0 4px 12px rgba(0,0,0,0.08); border-radius: 8px;'
      },
      legend: {
        orient: 'vertical',
        right: '5%',
        top: 'middle',
        textStyle: { color: '#666', fontSize: 13 },
        itemGap: 20,
        itemWidth: 12,
        itemHeight: 12
      },
      series: [{
        type: 'pie',
        radius: ['48%', '72%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 3
        },
        label: { show: false, position: 'center' },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: '600',
            color: '#333'
          }
        },
        labelLine: { show: false },
        data: levelData.length > 0 ? levelData : [
          { value: 0, name: '普通客户' },
          { value: 0, name: 'VIP 客户' },
          { value: 0, name: '重要客户' }
        ]
      }]
    })
  }

  // 合同金额统计图
  if (contractChart.value) {
    const chart = echarts.init(contractChart.value)
    chart.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        backgroundColor: 'rgba(255, 255, 255, 1)',
        borderColor: '#e8e8e8',
        borderWidth: 1,
        textStyle: { color: '#333', fontSize: 13 },
        padding: [14, 18],
        extraCssText: 'box-shadow: 0 4px 12px rgba(0,0,0,0.08); border-radius: 8px;'
      },
      grid: { left: '3%', right: '4%', bottom: '3%', top: '8%', containLabel: true },
      xAxis: {
        type: 'category',
        data: contractData.months.length > 0 ? contractData.months : ['暂无数据'],
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: '#999', fontSize: 12, margin: 16 }
      },
      yAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: '#f5f5f5' } },
        axisLabel: {
          color: '#999',
          fontSize: 12,
          formatter: (value) => {
            if (value >= 100000000) return '¥' + (value / 100000000).toFixed(1) + '亿'
            if (value >= 10000) return '¥' + (value / 10000).toFixed(0) + '万'
            return '¥' + value
          }
        },
        axisTick: { show: false },
        axisLine: { show: false }
      },
      series: [{
        data: contractData.amounts.length > 0 ? contractData.amounts : [0],
        type: 'bar',
        barWidth: '35%',
        itemStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: '#1890ff' },
              { offset: 1, color: '#69c0ff' }
            ]
          },
          borderRadius: [8, 8, 0, 0],
          shadowColor: 'rgba(24, 144, 255, 0.3)',
          shadowBlur: 10,
          shadowOffsetY: 4
        }
      }]
    })
  }
}

onMounted(() => {
  loadStats()
  initCharts()
  
  window.addEventListener('resize', () => {
    customerGrowthChart.value && echarts.getInstanceByDom(customerGrowthChart.value)?.resize()
    customerLevelChart.value && echarts.getInstanceByDom(customerLevelChart.value)?.resize()
    contractChart.value && echarts.getInstanceByDom(contractChart.value)?.resize()
  })
})
</script>

<style scoped>
.dashboard {
  padding: 32px;
  background: linear-gradient(180deg, #f7f9fc 0%, #f0f2f5 100%);
  min-height: calc(100vh - 84px);
}

/* 欢迎横幅 */
.welcome-section {
  margin-bottom: 32px;
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 50%, #69c0ff 100%);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.2);
  min-height: 120px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  min-height: 120px;
}

.welcome-title {
  font-size: 24px;
  color: #fff;
  margin: 0 0 8px 0;
  font-weight: 600;
  letter-spacing: -0.5px;
}

.welcome-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  font-weight: 400;
}

.welcome-date {
  display: flex;
  align-items: center;
  gap: 10px;
  color: rgba(255, 255, 255, 0.95);
  font-size: 14px;
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(10px);
  padding: 10px 18px;
  border-radius: 20px;
  font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  border: 1px solid rgba(0, 0, 0, 0.04);
  position: relative;
  height: 160px;
}

.stat-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.1);
  border-color: rgba(0, 0, 0, 0.08);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  height: 100%;
}

.stat-icon-wrapper {
  position: relative;
  width: 64px;
  height: 64px;
  flex-shrink: 0;
}

.stat-icon-bg {
  position: absolute;
  inset: 0;
  border-radius: 14px;
  opacity: 0.15;
}

.customer-card .stat-icon-bg {
  background: linear-gradient(135deg, #1890ff, #40a9ff);
}

.followup-card .stat-icon-bg {
  background: linear-gradient(135deg, #52c41a, #73d13d);
}

.opportunity-card .stat-icon-bg {
  background: linear-gradient(135deg, #faad14, #ffc53d);
}

.contract-card .stat-icon-bg {
  background: linear-gradient(135deg, #f5222d, #ff4d4f);
}

.stat-icon {
  position: relative;
  width: 64px;
  height: 64px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  z-index: 1;
}

.customer-card .stat-icon {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  box-shadow: 0 8px 20px rgba(24, 144, 255, 0.3);
}

.followup-card .stat-icon {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  box-shadow: 0 8px 20px rgba(82, 196, 26, 0.3);
}

.opportunity-card .stat-icon {
  background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%);
  box-shadow: 0 8px 20px rgba(250, 173, 20, 0.3);
}

.contract-card .stat-icon {
  background: linear-gradient(135deg, #f5222d 0%, #ff4d4f 100%);
  box-shadow: 0 8px 20px rgba(245, 34, 45, 0.3);
}

.stat-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
}

.stat-label {
  font-size: 13px;
  color: #8c8c8c;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #262626;
  letter-spacing: -1px;
  line-height: 1;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 500;
  margin-top: 4px;
}

.stat-trend.positive {
  color: #52c41a;
}

.trend-icon {
  font-weight: 700;
  font-size: 14px;
}

.stat-card-footer {
  height: 3px;
  background: transparent;
  transition: all 0.3s ease;
}

.stat-card:hover .stat-card-footer {
  background: linear-gradient(90deg, transparent, rgba(0, 0, 0, 0.06), transparent);
}

/* 图表卡片 */
.charts-row {
  margin-bottom: 24px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  height: 400px;
  display: flex;
  flex-direction: column;
}

.chart-card:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
}

.chart-card-large {
  padding: 24px;
  height: 420px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.chart-title-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  letter-spacing: -0.3px;
}

.chart-badge {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f6ffed, #d9f7be);
  color: #52c41a;
  font-weight: 500;
  border: 1px solid rgba(82, 196, 26, 0.2);
}

.chart-badge.warning {
  background: linear-gradient(135deg, #fff7e6, #ffe7ba);
  color: #faad14;
  border: 1px solid rgba(250, 173, 20, 0.2);
}

.chart-badge.primary {
  background: linear-gradient(135deg, #e6f7ff, #bae7ff);
  color: #1890ff;
  border: 1px solid rgba(24, 144, 255, 0.2);
}

.chart {
  flex: 1;
  width: 100%;
  min-height: 0;
}

.chart-large {
  flex: 1;
  width: 100%;
  min-height: 0;
}

/* 响应式 */
@media (max-width: 1400px) {
  .stat-value {
    font-size: 28px;
  }
  
  .stat-icon {
    width: 56px;
    height: 56px;
    font-size: 24px;
  }
}

@media (max-width: 768px) {
  .dashboard {
    padding: 20px;
  }
  
  .welcome-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .stats-row .el-col {
    margin-bottom: 16px;
  }
}
</style>
