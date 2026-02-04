if (import.meta.env.VITE_USE_MOCK) {
  const Mock = await import('mockjs')
  const { pathToRegexp } = await import('path-to-regexp')

  const MODULES = ['cust', 'system']

  MODULES.forEach(async (key) => {
    const mockList = await import(`./${key}/index.mock.ts`)
    for (const { url, method, response, timeout } of mockList.default) {
      __setupMock__(timeout)
      Mock.mock(__urlToRegexp__(url), method || 'get', __XHR2ExpressReqWrapper__(response))
    }
  })

  function __urlToRegexp__(url) {
    const reg = pathToRegexp(url)
    return new RegExp(reg.source.substring(1), reg.flags)
  }
  function __param2Obj__(url) {
    const search = url.split('?')[1]
    if (!search) {
      return {}
    }
    return JSON.parse(
      '{"' +
        decodeURIComponent(search)
          .replace(/"/g, '\\"')
          .replace(/&/g, '","')
          .replace(/=/g, '":"')
          .replace(/\+/g, ' ') +
        '"}'
    )
  }
  function __XHR2ExpressReqWrapper__(handle) {
    return function (options) {
      let result = null
      if (typeof handle === 'function') {
        const { body, type, url, headers } = options
        let b = body
        try {
          b = JSON.parse(body)
        } catch (e) {
          throw e
        }
        result = handle({
          method: type,
          body: b,
          query: __param2Obj__(url),
          headers
        })
      } else {
        result = handle
      }
      return Mock.mock(result)
    }
  }
  function __setupMock__(timeout = 0) {
    timeout &&
      Mock.setup({
        timeout
      })
  }
}
