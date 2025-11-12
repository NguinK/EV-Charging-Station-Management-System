import { Form, Input, Button, DatePicker, Select, message } from "antd";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import { useDispatch, useSelector } from "react-redux";
import { startReservation } from "../features/reservationSlice";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import chargingStationAPI from "../api/chargingStationAPI";
dayjs.extend(utc);

function BookingPage() {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const dispatch = useDispatch();
  const { loading } = useSelector((state) => state.reservation);
  // const [stations, setStations] = useState([]);

  // ✅ Lấy danh sách trạm sạc khi component mount
  // useEffect(() => {
  //   const fetchStations = async () => {
  //     try {
  //       const res = await chargingStationAPI.getAllStations();
  //       setStations(res.data || []); // res.data = mảng trạm sạc
  //     } catch (error) {
  //       message.error("Không thể tải danh sách trạm sạc!");
  //       console.error("Lỗi getAllStations:", error);
  //     }
  //   };

  //   fetchStations();
  // }, []);
  // ✅ State lưu danh sách trạm
  const [stations, setStations] = useState([]);

  // ✅ Lấy danh sách trạm sạc khi component mount
  useEffect(() => {
    const fetchStations = async () => {
      try {
        const res = await chargingStationAPI.getAllStations();
        setStations(res.data || []);
      } catch (error) {
        message.error("Không thể tải danh sách trạm sạc!");
        console.error("Lỗi getAllStations:", error);
      }
    };
    fetchStations();
  }, []);

  // ✅ Gửi form
  const onFinish = async (values) => {
    const start = values.endTime; // đây là dayjs object từ DatePicker

    if (!start || !dayjs(start).isValid()) {
      message.error("Vui lòng chọn thời gian bắt đầu hợp lệ!");
      return;
    }
    const startIsoUtc = dayjs(start).toDate().toISOString();

    const payload = {
      stationId: values.stationName,
      connectorType: values.connectorType,
      endTime: startIsoUtc,
    };

    console.log("Payload gửi Redux:", payload);

    dispatch(startReservation(payload))
      .unwrap()
      .then((res) => {
        message.success("Đặt xe thành công!");
        console.log("Kết quả API:", res);
        // ✅ Lưu ID đặt xe theo tài khoản hiện tại
        const userInfo = JSON.parse(localStorage.getItem("userInfo"));
        if (userInfo && userInfo.id) {
          const key = `latestBookingId_${userInfo.id}`;
          localStorage.setItem(key, res.reservationId);
        }
        form.resetFields();

        localStorage.setItem("latestBookingId", res.reservationId);
        setTimeout(() => {
          navigate("/user");
        }, 300);
      })
      .catch((err) => {
        console.error("Lỗi khi gửi yêu cầu:", err);
        message.error("Đặt xe thất bại!");
      });
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={onFinish}
      style={{ maxWidth: 500, margin: "0 auto", marginTop: 30 }}
    >
      {/* ✅ Danh sách trạm sạc */}
      <Form.Item
        label="Station"
        name="stationName"
        rules={[{ required: true, message: "Please select station" }]}
      >
        <Select
          placeholder="Station"
          loading={stations.length === 0}
          showSearch
          optionFilterProp="children"
        >
          {stations
            .filter((s) => s.status !== "OFFLINE")
            .map((station) => (
              <Select.Option key={station.id} value={station.id}>
                {station.name} – {" "}
                <span
                style={{
                  color: station.status === "OFFLINE" ? "red" : "green",
                  fontWeight: 500,
                }}
              >
                {station.status}
              </span>
              </Select.Option>
              
            ))}
        </Select>
      </Form.Item>
      {/* ✅ Loại cổng sạc */}

      <Form.Item
        label="Connector Type "
        name="connectorType"
      >
        <Select placeholder="Select connector type">
          <Select.Option value="CCS">CCS</Select.Option>
          <Select.Option value="CHADEMO">CHADEMO</Select.Option>
          <Select.Option value="AC">AC</Select.Option>
        </Select>
      </Form.Item>

      <Form.Item label="Time " name="endTime">
        <DatePicker
          showTime={{ format: "HH:mm", minuteStep: 15 }}
          format="YYYY-MM-DD HH:mm"
          style={{ width: "32%" }}
          disabledDate={(current) =>
            current && current < dayjs().startOf("day")
          }
        />
      </Form.Item>

      <Form.Item>
        <Button
          type="primary"
          htmlType="submit"
          block
          loading={loading}
          className="h-15 w-full rounded-full border-none bg-gradient-to-r  text-base font-semibold text-[#fcfcfc] shadow-[0_15px_30px_rgba(18,70,38,0.35)] "
        >
          Submit
        </Button>
      </Form.Item>
    </Form>
  );
}

export default BookingPage;
