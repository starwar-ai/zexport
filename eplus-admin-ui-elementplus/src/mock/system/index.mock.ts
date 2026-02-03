export default [
  // 模拟payment-term列表接口
  {
    url: '/admin-api/payment-term/list',
    method: 'get',
    response: () => {
      return {
        code: 0,
        data: [
          {
            id: 1,
            name: '方式1',
            nameEng: '',
            createTime: ''
          },
          {
            id: 2,
            name: '方式二',
            nameEng: '',
            createTime: ''
          }
        ]
      }
    }
  }
]
