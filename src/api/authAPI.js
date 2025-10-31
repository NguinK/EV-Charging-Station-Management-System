import axiosClient from "./axiosClient";

import React from 'react'

const  authAPI = {
   postLoginUser : (data) => axiosClient.post('/auth/login',data),
   postResgisterUser : (data) => axiosClient.post('/auth/register-driver',data),
}

export default authAPI