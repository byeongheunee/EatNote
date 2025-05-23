// src/plugins/axios.js
import axios from 'axios'

export const setupAxios = (auth) => {
  axios.interceptors.request.use(
    (config) => {
      const token = auth.accessToken
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    },
  )
}

export default axios
