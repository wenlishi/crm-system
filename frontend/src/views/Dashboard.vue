<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1 class="welcome-title">欢迎回来，管理员</h1>
          <p class="welcome-subtitle">今天是个好日子，继续完成销售目标吧！</p>
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
              <div class="stat-trend positive">
                <span class="trend-icon">↑</span>
                <span>今日新增 {{ stats.todayCustomers }} 人</span>
              </div>
            </div>
          </div>
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
              <div class="stat-trend positive">
                <span class="trend-icon">↑</span>
                <span>本周 {{ stats.weekFollowUps }} 次</span>
              </div>
            </div>
          </div>
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
              <div class="stat-trend positive">
                <span class="trend-icon">↑</span>
                <span>本月新增 {{ stats.newOpportunities }} 个</span>
              </div>
            </div>
          </div>
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
              <div class="stat-trend positive">
                <span class="trend-icon">↑</span>
                <span>本月 {{ stats.monthContractAmount }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 第一行图表 -->
    <el-row :gutter="16" class="charts-row">
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <span class="chart-title">📈 客户增长趋势</span>
              <span class="chart-badge">近 30 天</span>
            </div>
          </div>
          <div ref="customerGrowthChart" class="chart"></div>
        </div>
      </el-col>
      
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <span class="chart-title">🎯 客户级别分布</span>
              <span class="chart-badge warning">实时</span>
            </div>
          </div>
          <div ref="customerLevelChart" class="chart"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 第二行图表 -->
    <el-row :gutter="16" class="charts-row">
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <span class="chart-title">💰 合同金额统计</span>
              <span class="chart-badge primary">年度</span>
            </div>
          </div>
          <div ref="contractChart" class="chart"></div>
        </div>
      </el-col>
      
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <span class="chart-title">📊 销售漏斗</span>
              <span class="chart-badge" style="background: linear-gradient(135deg, #fff1f0, #ffccc7); color: #f5222d; border: 1px solid rgba(245, 34, 45, 0.2);">转化分析</span>
            </div>
          </div>
          <div ref="salesFunnelChart" class="chart"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 第三行图表 -->
    <el-row :gutter="16" class="charts-row">
      <el-col :span="24">
        <div class="chart-card chart-card-large">
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <span class="chart-title">📉 线索来源分析</span>
              <span class="chart-badge" style="background: linear-gradient(135deg, #f9f0ff, #efdbff); color: #722ed1; border: 1px solid rgba(114, 46, 209, 0.2);">多渠道</span>
            </div>
          </div>
          <div ref="leadSourceChart" class="chart-large"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { User, Document, Money, DocumentCopy, Calendar } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 统计数据
const stats = reactive({
  totalCustomers: 1268,
  totalFollowUps: 3456,
  totalOpportunities: 256,
  totalContractAmount: 8976000,
  todayCustomers: 18,
  weekFollowUps: 142,
  newOpportunities: 32,
  monthContractAmount: '¥126.5 万'
})

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const weekDay = weekDays[now.getDay()]
  return `${year}年${month}月${day}日 ${weekDay}`
})

// 图表引用
const customerGrowthChart = ref(null)
const customerLevelChart = ref(null)
const contractChart = ref(null)
const salesFunnelChart = ref(null)
const leadSourceChart = ref(null)

// 格式化金额
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

// 格式化数字
const formatNumber = (value) => {
  if (!value) return '0'
  const num = Number(value)
  if (num >= 100000000) return (num / 100000000).toFixed(2) + '亿'
  if (num >= 10000) return (num / 10000).toFixed(2) + '万'
  return num.toLocaleString('zh-CN')
}

// 生成近 30 天日期
const generateDates = (days = 30) => {
  const dates = []
  for (let i = days - 1; i >= 0; i--) {
    const d = new Date()
    d.setDate(d.getDate() - i)
    dates.push(`${d.getMonth() + 1}/${d.getDate()}`)
  }
  return dates
}

// 生成模拟数据
const generateGrowthData = () => {
  const data = []
  for (let i = 0; i < 30; i++) {
    data.push(Math.floor(Math.random() * 50) + 20)
  }
  return data
}

// 初始化客户增长趋势图
const initCustomerGrowthChart = () => {
  if (!customerGrowthChart.value) return
  
  const chart = echarts.init(customerGrowthChart.value)
  const dates = generateDates(30)
  const counts = generateGrowthData()
  
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
      data: dates,
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
      data: counts,
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

// 初始化客户级别分布图
const initCustomerLevelChart = () => {
  if (!customerLevelChart.value) return
  
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
      data: [
        { value: 680, name: '普通客户', itemStyle: { color: '#1890ff' } },
        { value: 420, name: 'VIP 客户', itemStyle: { color: '#722ed1' } },
        { value: 168, name: '重要客户', itemStyle: { color: '#faad14' } }
      ]
    }]
  })
}

// 初始化合同金额统计图
const initContractChart = () => {
  if (!contractChart.value) return
  
  const chart = echarts.init(contractChart.value)
  
  const months = ['1 月', '2 月', '3 月', '4 月', '5 月', '6 月', '7 月', '8 月', '9 月', '10 月', '11 月', '12 月']
  const amounts = [680000, 720000, 850000, 920000, 780000, 950000, 1020000, 880000, 960000, 1100000, 1250000, 1380000]
  
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
      data: months,
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
          if (value >= 1000000) return '¥' + (value / 1000000).toFixed(1) + '百万'
          if (value >= 10000) return '¥' + (value / 10000).toFixed(0) + '万'
          return '¥' + value
        }
      },
      axisTick: { show: false },
      axisLine: { show: false }
    },
    series: [{
      data: amounts,
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

// 初始化销售漏斗图
const initSalesFunnelChart = () => {
  if (!salesFunnelChart.value) return
  
  const chart = echarts.init(salesFunnelChart.value)
  
  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 1)',
      borderColor: '#e8e8e8',
      borderWidth: 1,
      textStyle: { color: '#333', fontSize: 13 },
      padding: [14, 18],
      extraCssText: 'box-shadow: 0 4px 12px rgba(0,0,0,0.08); border-radius: 8px;'
    },
    series: [{
      type: 'funnel',
      left: '10%',
      top: '5%',
      bottom: '5%',
      right: '10%',
      width: '80%',
      min: 0,
      max: 100,
      minSize: '0%',
      maxSize: '100%',
      sort: 'descending',
      gap: 2,
      label: {
        show: true,
        position: 'inside',
        color: '#fff',
        fontSize: 13,
        fontWeight: 500
      },
      itemStyle: {
        borderColor: '#fff',
        borderWidth: 2
      },
      data: [
        { value: 100, name: '潜在客户', itemStyle: { color: '#1890ff' } },
        { value: 80, name: '意向客户', itemStyle: { color: '#13c2c2' } },
        { value: 60, name: '商机', itemStyle: { color: '#faad14' } },
        { value: 40, name: '谈判中', itemStyle: { color: '#f5222d' } },
        { value: 25, name: '成交', itemStyle: { color: '#52c41a' } }
      ]
    }]
  })
}

// 初始化线索来源分析图
const initLeadSourceChart = () => {
  if (!leadSourceChart.value) return
  
  const chart = echarts.init(leadSourceChart.value)
  
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
    legend: {
      data: ['网络营销', '电话咨询', '客户推荐', '展会活动', '其他'],
      bottom: '0%',
      textStyle: { color: '#666', fontSize: 12 },
      itemGap: 20,
      itemWidth: 12,
      itemHeight: 12
    },
    grid: { left: '3%', right: '4%', bottom: '12%', top: '8%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['1 月', '2 月', '3 月', '4 月', '5 月', '6 月', '7 月', '8 月', '9 月', '10 月', '11 月', '12 月'],
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
    series: [
      {
        name: '网络营销',
        type: 'bar',
        stack: 'total',
        barWidth: '35%',
        itemStyle: {
          color: '#1890ff',
          borderRadius: [0, 0, 0, 0]
        },
        data: [120, 132, 101, 134, 90, 230, 210, 180, 200, 220, 250, 280]
      },
      {
        name: '电话咨询',
        type: 'bar',
        stack: 'total',
        itemStyle: { color: '#13c2c2' },
        data: [220, 182, 191, 234, 290, 330, 310, 280, 300, 320, 350, 380]
      },
      {
        name: '客户推荐',
        type: 'bar',
        stack: 'total',
        itemStyle: { color: '#722ed1' },
        data: [150, 212, 201, 154, 190, 330, 410, 380, 400, 420, 450, 480]
      },
      {
        name: '展会活动',
        type: 'bar',
        stack: 'total',
        itemStyle: { color: '#faad14' },
        data: [80, 72, 71, 74, 90, 130, 110, 100, 120, 140, 160, 180]
      },
      {
        name: '其他',
        type: 'bar',
        stack: 'total',
        itemStyle: { color: '#d9d9d9' },
        data: [30, 42, 51, 54, 60, 70, 80, 75, 85, 95, 105, 115]
      }
    ]
  })
}

// 初始化所有图表
const initAllCharts = () => {
  initCustomerGrowthChart()
  initCustomerLevelChart()
  initContractChart()
  initSalesFunnelChart()
  initLeadSourceChart()
}

// 窗口大小变化时重新渲染图表
const handleResize = () => {
  customerGrowthChart.value && echarts.getInstanceByDom(customerGrowthChart.value)?.resize()
  customerLevelChart.value && echarts.getInstanceByDom(customerLevelChart.value)?.resize()
  contractChart.value && echarts.getInstanceByDom(contractChart.value)?.resize()
  salesFunnelChart.value && echarts.getInstanceByDom(salesFunnelChart.value)?.resize()
  leadSourceChart.value && echarts.getInstanceByDom(leadSourceChart.value)?.resize()
}

onMounted(() => {
  initAllCharts()
  window.addEventListener('resize', handleResize)
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
  
  .chart-card {
    height: 350px;
  }
}
</style>
