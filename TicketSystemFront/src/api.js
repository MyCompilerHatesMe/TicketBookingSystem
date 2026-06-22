import { getToken } from './auth'

const BASE = 'http://localhost:8080/api/v1'

async function parseResponse(res) {
  let data
  try {
    const text = await res.text()
    try {
      data = JSON.parse(text)
    } catch {
      data = text
    }
  } catch {
    data = { message: 'Failed to read response body' }
  }
  return { status: res.status, statusText: res.statusText, data }
}

export async function apiPost(path, body) {
  const headers = {
    'Content-Type': 'application/json',
  }
  const token = getToken()
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  const res = await fetch(`${BASE}${path}`, {
    method: 'POST',
    headers,
    body: JSON.stringify(body),
  })
  return parseResponse(res)
}

export async function apiGet(path, params = {}) {
  const qs = new URLSearchParams(
    Object.fromEntries(Object.entries(params).filter(([, v]) => v !== '' && v != null))
  ).toString()
  const url = `${BASE}${path}${qs ? '?' + qs : ''}`
  
  const headers = {}
  const token = getToken()
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  
  const res = await fetch(url, { headers })
  return parseResponse(res)
}
