<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card customer-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCustomers }}</div>
              <div class="stat-label">客户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card followup-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalFollowUps }}</div>
              <div class="stat-label">跟进记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card opportunity-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalOpportunities }}</div>
              <div class="stat-label">商机数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card contract-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><DocumentCopy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ formatMoney(stats.totalContractAmount) }}</div>
              <div class="stat-label">合同金额</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>客户增长趋势</span>
            </div>
          </template>
          <div ref="customerGrowthChart" class="chart"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>客户级别分布</span>
            </div>
          </template>
          <div ref="customerLevelChart" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>合同金额统计</span>
            </div>
          </template>
          <div ref="contractChart" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats, getCustomerGrowthStats, getCustomerStats, getContractStats } from '@/api/statistics'

const stats = reactive({
  totalCustomers: 0,
  totalFollowUps: 0,
  totalOpportunities: 0,
  totalContractAmount: 0
})

const customerGrowthChart = ref(null)
const customerLevelChart = ref(null)
const contractChart = ref(null)

let growthChartInstance = null
let levelChartInstance = null
let contractChartInstance = null

const formatMoney = (value) => {
  return Number(value || 0).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const loadStats = async () => {
  try {
    const res = await getDashboardStats()
    // 适配后端返回的数据结构
    if (res.data) {
      stats.totalCustomers = res.data.totalCustomers || 0
      stats.totalFollowUps = res.data.totalFollowUps || 0
      stats.totalOpportunities = res.data.totalOpportunities || 0
      stats.totalContractAmount = res.data.totalContractAmount || 0
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const initCustomerGrowthChart = async () => {
  if (!customerGrowthChart.value) return
  
  try {
    const res = await getCustomerGrowthStats()
    // 适配后端返回的数据结构：{ dailyStats: [{date, count}, ...] }
    const growthData = res.data?.dailyStats || []
    
    growthChartInstance = echarts.init(customerGrowthChart.value)
    
    const option = {
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: growthData.map(item => item.date)
      },
      yAxis: { type: 'value' },
      series: [{
        data: growthData.map(item => item.count),
        type: 'line',
        smooth: true,
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        }
      }]
    }
    
    growthChartInstance.setOption(option)
  } catch (error) {
    console.error('加载客户增长图表失败:', error)
  }
}

const initCustomerLevelChart = async () => {
  if (!customerLevelChart.value) return
  
  // TODO: 需要后端添加按客户级别统计接口
  // 暂时使用模拟数据
  try {
    // const res = await getCustomerStats()
    // const levelData = res.data?.byLevel || []
    
    // 临时使用模拟数据
    const levelData = [
      { level: 'A', count: 25 },
      { level: 'B', count: 45 },
      { level: 'C', count: 30 },
      { level: 'D', count: 10 }
    ]
    
    levelChartInstance = echarts.init(customerLevelChart.value)
    
    const option = {
      tooltip: { trigger: 'item' },
      legend: { top: '5%', left: 'center' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: levelData.map(item => ({
          value: item.count,
          name: item.level + '类客户'
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    }
    
    levelChartInstance.setOption(option)
  } catch (error) {
    console.error('加载客户级别图表失败:', error)
  }
}

const initContractChart = async () => {
  if (!contractChart.value) return
  
  try {
    const res = await getContractStats()
    // 显示合同总金额（仪表盘）
    const totalAmount = res.data?.totalAmount || 0
    
    contractChartInstance = echarts.init(contractChart.value)
    
    // 使用仪表盘展示合同总金额
    const option = {
      tooltip: { trigger: 'item' },
      series: [{
        type: 'gauge',
        startAngle: 180,
        endAngle: 0,
        min: 0,
        max: 1000000,
        splitNumber: 5,
        itemStyle: {
          color: '#67C23A'
        },
        progress: {
          show: true,
          width: 30
        },
        pointer: {
          show: false
        },
        axisLine: {
          lineStyle: {
            width: 30
          }
        },
        axisTick: {
          show: false
        },
        splitLine: {
          length: 15,
          lineStyle: {
            width: 2,
            color: '#999'
          }
        },
        axisLabel: {
          distance: 25,
          color: '#999',
          fontSize: 10,
          formatter: (value) => {
            if (value >= 10000) {
              return (value / 10000) + '万'
            }
            return value
          }
        },
        anchor: {
          show: false
        },
        title: {
          show: true,
          text: '合同总金额',
          offsetCenter: [0, '30%'],
          color: '#333',
          fontSize: 14
        },
        detail: {
          show: true,
          valueAnimation: true,
          width: 100,
          height: 40,
          borderRadius: 8,
          borderWidth: 0,
          borderColor: '#67C23A',
          backgroundColor: '#f0f9ff',
          offsetCenter: [0, '0%'],
          fontSize: 20,
          color: '#67C23A',
          formatter: (value) => '¥' + Number(value).toLocaleString()
        },
        data: [{
          value: totalAmount,
          name: '总计'
        }]
      }]
    }
    
    contractChartInstance.setOption(option)
  } catch (error) {
    console.error('加载合同图表失败:', error)
  }
}

onMounted(async () => {
  loadStats()
  
  setTimeout(async () => {
    await initCustomerGrowthChart()
    await initCustomerLevelChart()
    await initContractChart()
  }, 100)
  
  window.addEventListener('resize', () => {
    growthChartInstance?.resize()
    levelChartInstance?.resize()
    contractChartInstance?.resize()
  })
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.customer-card .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.followup-card .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.opportunity-card .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.contract-card .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 8px;
}

.card-header {
  font-weight: 500;
  color: #333;
}

.chart {
  height: 300px;
  width: 100%;
}
</style>
