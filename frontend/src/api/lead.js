import request from '@/utils/request'

/**
 * 获取线索列表
 * @param {Object} params - 查询参数
 * @param {number} params.current - 当前页码
 * @param {number} params.size - 每页数量
 * @param {number} params.status - 线索状态
 * @param {number} params.level - 线索级别
 * @param {string} params.source - 线索来源
 * @param {number} params.ownerId - 负责人 ID
 */
export function getLeadList(params) {
  return request({
    url: '/lead/list',
    method: 'get',
    params
  })
}

/**
 * 获取线索详情
 * @param {number} id - 线索 ID
 */
export function getLeadDetail(id) {
  return request({
    url: `/lead/${id}`,
    method: 'get'
  })
}

/**
 * 创建线索
 * @param {Object} data - 线索数据
 */
export function createLead(data) {
  return request({
    url: '/lead',
    method: 'post',
    data
  })
}

/**
 * 更新线索
 * @param {number} id - 线索 ID
 * @param {Object} data - 线索数据
 */
export function updateLead(id, data) {
  return request({
    url: `/lead/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除线索
 * @param {number} id - 线索 ID
 */
export function deleteLead(id) {
  return request({
    url: `/lead/${id}`,
    method: 'delete'
  })
}

/**
 * 线索转化为客户
 * @param {number} id - 线索 ID
 * @param {Object} data - 转化数据
 */
export function convertToCustomer(id, data) {
  return request({
    url: `/lead/${id}/convert`,
    method: 'post',
    data
  })
}

/**
 * 关闭线索
 * @param {number} id - 线索 ID
 * @param {string} reason - 关闭原因
 */
export function closeLead(id, reason) {
  return request({
    url: `/lead/${id}/close?reason=${encodeURIComponent(reason)}`,
    method: 'post'
  })
}

/**
 * 获取今日待跟进线索
 */
export function getTodayFollowUp() {
  return request({
    url: '/lead/follow-up/today',
    method: 'get'
  })
}

/**
 * 获取线索统计
 */
export function getLeadStats() {
  return request({
    url: '/lead/stats',
    method: 'get'
  })
}

/**
 * 获取线索跟进历史
 * @param {number} leadId - 线索 ID
 */
export function getLeadFollowUpHistory(leadId) {
  return request({
    url: `/lead/${leadId}/follow-up`,
    method: 'get'
  })
}

/**
 * 添加线索跟进记录
 * @param {Object} data - 跟进记录数据
 */
export function addLeadFollowUp(data) {
  return request({
    url: '/lead/follow-up',
    method: 'post',
    data
  })
}
