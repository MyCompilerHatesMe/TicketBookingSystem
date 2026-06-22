let inMemoryToken = null

export function getToken() {
  if (!inMemoryToken) {
    inMemoryToken = localStorage.getItem('tbs_token')
  }
  return inMemoryToken
}

export function setToken(token) {
  inMemoryToken = token
  if (token) {
    localStorage.setItem('tbs_token', token)
  } else {
    localStorage.removeItem('tbs_token')
  }
}

export function clearToken() {
  inMemoryToken = null
  localStorage.removeItem('tbs_token')
}

export function getUser() {
  const token = getToken()
  if (!token) return null
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    const payload = JSON.parse(jsonPayload)
    return {
      username: payload.sub,
      roles: payload.roles || [],
    }
  } catch {
    return null
  }
}
