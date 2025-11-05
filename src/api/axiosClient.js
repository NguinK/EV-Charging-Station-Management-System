import axios from "axios";

const axiosClient = axios.create({
    baseURL:"http://localhost:8080", // day la URL goc API  moi goi la phai xu ly CORS cross-origin resource sharing
    headers: {
        "Content-Type":"application/json", // ban gui du lieu dang json 

    } ,
    timeout: 1000, // thoi gian cho phep de ket noi toi API
})
// Interceptor la 1 ham duoc goi truoc khi gui request di 
axiosClient.interceptors.request.use( // Dung can thiep request truoc khi gui di 
    (config) => {
        const token = localStorage.getItem("token");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
        
)

export default axiosClient;
