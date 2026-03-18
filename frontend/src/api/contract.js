import request from '@/utils/request'

// 分页查询合同
export function getContractList(params) {
  return request({
    url: '/contracts/page',
    method: 'get',
    params
  })
}

// 查询合同详情
export function getContractDetail(id) {
  return request({
    url: `/contracts/${id}`,
    method: 'get'
  })
}

// 新增合同
export function createContract(data) {
  return request({
    url: '/contracts',
    method: 'post',
    data
  })
}

// 修改合同
export function updateContract(data) {
  return request({
    url: '/contracts',
    method: 'put',
    data
  })
}

// 删除合同
export function deleteContract(id) {
  return request({
    url: `/contracts/${id}`,
    method: 'delete'
  })
}

// 更新合同状态
export function updateContractStatus(id, status) {
  return request({
    url: `/contracts/${id}/status?status=${status}`,
    method: 'put'
  })
}

// 按客户 ID 查询合同
export function getContractByCustomerId(customerId) {
  return request({
    url: `/contracts/customer/${customerId}`,
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
