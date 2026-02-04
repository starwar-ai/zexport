const SUCCESS_CODE = 0

const timeout = 1000

const List: {
  username: string
  password: string
  role: string
  roleId: string
  permissions: string | string[]
}[] = [
  {
    username: 'admin',
    password: 'admin',
    role: 'admin',
    roleId: '1',
    permissions: ['*.*.*']
  },
  {
    username: 'test',
    password: 'test',
    role: 'test',
    roleId: '2',
    permissions: ['example:dialog:create', 'example:dialog:delete']
  }
]

const WORD_DICT = '01234567890ABCDEFGHIGKLMNOPQRSTUVWXYZ'
const random = (len: number) => {
  let res = ''
  while (res.length < len) res += WORD_DICT[Math.floor(Math.random() * WORD_DICT.length)]
  return res
}
export default [
  // 列表接口
  {
    url: '/admin-api/cust/list',
    method: 'get',
    timeout,
    response: ({ query }) => {
      const { username, pageIndex = 1, pageSize = 10 } = query

      const mockList = List.filter((item) => {
        if (username && item.username.indexOf(username) < 0) return false
        return true
      })
      const pageList = mockList.filter(
        (_, index) => index < pageSize * pageIndex && index >= pageSize * (pageIndex - 1)
      )

      return {
        code: SUCCESS_CODE,
        data: {
          total: mockList.length,
          list: pageList
        }
      }
    }
  },
  {
    url: '/admin-api/crm/cust/code',
    method: 'get',
    timeout,
    response: () => {
      return {
        code: SUCCESS_CODE,
        data: random(5)
      }
    }
  }
]
