import request from '@/utils/request'

// 分页查询跟进记录
export function getFollowUpList(params) {
  return request({
    url: '/follow-ups/page',
    method: 'get',
    params
  })
}

// 查询跟进记录详情
export function getFollowUpDetail(id) {
  return request({
    url: `/follow-ups/${id}`,
    method: 'get'
  })
}

// 新增跟进记录
export function createFollowUp(data) {
  return request({
    url: '/follow-ups',
    method: 'post',
    data
  })
}

// 修改跟进记录
export function updateFollowUp(data) {
  return request({
    url: '/follow-ups',
    method: 'put',
    data
  })
}

// 删除跟进记录
export function deleteFollowUp(id) {
  return request({
    url: `/follow-ups/${id}`,
    method: 'delete'
  })
}

// 按客户 ID 查询跟进记录
export function getFollowUpByCustomerId(customerId) {
  return request({
    url: `/follow-ups/customer/${customerId}`,
    method: 'get'
  })
}
