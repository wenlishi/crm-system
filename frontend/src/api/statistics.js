import request from '@/utils/request'

// 获取仪表盘统计
export function getDashboardStats() {
  return request({
    url: '/statistics/dashboard',
    method: 'get'
  })
}

// 获取客户增长统计（按月）
export function getCustomerGrowthStats(params) {
  return request({
    url: '/statistics/customer-growth',
    method: 'get',
    params
  })
}

// 获取跟进统计
export function getFollowUpStats() {
  return request({
    url: '/statistics/follow-up',
    method: 'get'
  })
}

// 获取客户统计（按级别）
export function getCustomerStats() {
  return request({
    url: '/statistics/customer',
    method: 'get'
  })
}

// 获取合同统计
export function getContractStats() {
  return request({
    url: '/contracts/stats',
    method: 'get'
  })
}
