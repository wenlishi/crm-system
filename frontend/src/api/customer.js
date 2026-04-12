import request from '@/utils/request'

// 分页查询客户
export function getCustomerList(params) {
  return request({
    url: '/customers/page',
    method: 'get',
    params
  })
}

// 查询客户详情
export function getCustomerDetail(id) {
  return request({
    url: `/customers/${id}`,
    method: 'get'
  })
}

// 新增客户
export function createCustomer(data) {
  return request({
    url: '/customers',
    method: 'post',
    data
  })
}

// 修改客户
export function updateCustomer(data) {
  return request({
    url: '/customers',
    method: 'put',
    data
  })
}

// 删除客户
export function deleteCustomer(id) {
  return request({
    url: `/customers/${id}`,
    method: 'delete'
  })
}

// 获取所有客户（下拉框用）
export function getAllCustomers() {
  return request({
    url: '/customers',
    method: 'get',
    params: { all: true }
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
    url: '/customers/batch-assign',
    method: 'post',
    data
  })
}
