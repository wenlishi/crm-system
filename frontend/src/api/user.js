import request from '@/utils/request'

// 分页查询用户
export function getUserList(params) {
  return request({
    url: '/users/page',
    method: 'get',
    params
  })
}

// 查询用户详情
export function getUserDetail(id) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

// 新增用户
export function createUser(data) {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/users',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

// 批量删除用户
export function batchDeleteUsers(ids) {
  return request({
    url: '/users',
    method: 'delete',
    params: { ids: ids.join(',') }
  })
}

// 更新用户状态
export function updateUserStatus(id, status) {
  return request({
    url: `/users/${id}/status?status=${status}`,
    method: 'put'
  })
}

// 重置用户密码
export function resetUserPassword(id) {
  return request({
    url: `/users/${id}/password/reset`,
    method: 'put'
  })
}

// 修改自己的密码
export function changePassword(oldPassword, newPassword) {
  return request({
    url: '/users/password',
    method: 'put',
    params: { oldPassword, newPassword }
  })
}

// 为用户分配角色
export function assignUserRoles(userId, roleIds) {
  return request({
    url: `/users/${userId}/roles`,
    method: 'post',
    params: { roleIds: roleIds.join(',') }
  })
}
