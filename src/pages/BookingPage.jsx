import React, { useEffect, useState } from "react";
import { Form, Input, Button, DatePicker, Select, message } from "antd";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import { useDispatch, useSelector } from "react-redux";
import { startReservation } from "../features/reservationSlice";
import chargingStationAPI from "../api/chargingStationAPI";
dayjs.extend(utc);

function BookingPage() {
  const [form] = Form.useForm();
  const dispatch = useDispatch();
  const { loading } = useSelector((state) => state.reservation);
  const [stations, setStations] = useState([]);

  // ✅ Lấy danh sách trạm sạc khi component mount
  useEffect(() => {
    const fetchStations = async () => {
      try {
        const res = await chargingStationAPI.getAllStations();
        setStations(res.data || []); // res.data = mảng trạm sạc
      } catch (error) {
        message.error("Không thể tải danh sách trạm sạc!");
        console.error("Lỗi getAllStations:", error);
      }
    };

    fetchStations();
  }, []);

  // ✅ Gửi form
  const onFinish = async (values) => {
    const startIso = dayjs(values.startTime)
      .utc()
      .format("YYYY-MM-DDTHH:mm:ss[Z]");
    const payload = {
      stationId: Number(values.stationId),
      connectorType: values.connectorType,
      startTime: startIso,
    };

    console.log("Payload gửi Redux:", payload);

    dispatch(startReservation(payload))
      .unwrap()
      .then((res) => {
        message.success("Đặt xe thành công!");
        console.log("Kết quả API:", res);
        form.resetFields();
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
      {/* ✅ Trạm sạc chọn từ danh sách */}
      {/* <Form.Item
        label="Trạm sạc"
        name="stationId"
        rules={[{ required: true, message: "Vui lòng chọn trạm sạc!" }]}
      >
        <Select
          placeholder="Chọn trạm sạc"
          loading={stations.length === 0}
          showSearch
          optionFilterProp="children"
        >
          {stations.map((station) => (
            <Select.Option key={station.id} value={station.id}>
              {station.name} – {station.location}
            </Select.Option>
          ))}
        </Select>
      </Form.Item> */}
      <Form.Item
        label="Mã trạm "
        name="stationId"
        rules={[{ required: true, message: "Vui lòng nhập mã trạm!" }]}
      >
        <Input type="number" placeholder="Nhập mã trạm..." />
      </Form.Item>
      {/* ✅ Loại cổng sạc */}

      <Form.Item
        label="Loại cổng sạc (connectorType)"
        name="connectorType"
        rules={[{ message: "Vui lòng chọn loại cổng sạc!" }]}
      >
        <Select placeholder="Chọn loại cổng">
          <Select.Option value="CCS">CCS</Select.Option>
          <Select.Option value="CHADEMO">CHADEMO</Select.Option>
          <Select.Option value="AC">AC</Select.Option>
        </Select>
      </Form.Item>

      <Form.Item label="Thời gian bắt đầu " name="startTime">
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
