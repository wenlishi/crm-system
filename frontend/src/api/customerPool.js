import request from '@/utils/request'

/**
 * 获取公海池列表
 * @param {Object} params - 查询参数
 * @param {number} params.current - 当前页码
 * @param {number} params.size - 每页数量
 * @param {string} params.customerName - 客户名称
 * @param {string} params.level - 客户级别
 * @param {string} params.reason - 掉入原因
 */
export function getPoolList(params) {
  return request({
    url: '/customer-pool/page',
    method: 'get',
    params
  })
}

/**
 * 领取客户
 * @param {Object} data - 领取数据
 * @param {Array} data.customerIds - 客户 ID 列表
 */
export function claimCustomer(data) {
  return request({
    url: '/customer-pool/claim',
    method: 'post',
    data
  })
}

/**
 * 批量分配客户
 * @param {Object} data - 分配数据
 * @param {Array} data.customerIds - 客户 ID 列表
 * @param {number} data.ownerId - 负责人 ID
 * @param {string} data.remark - 分配说明
 */
export function batchAssignCustomer(data) {
  return request({
    url: '/customer-pool/batch-assign',
    method: 'post',
    data
  })
}

/**
 * 获取我的客户数
 */
export function getMyCustomerCount() {
  return request({
    url: '/customer-pool/my-count',
    method: 'get'
  })
}
