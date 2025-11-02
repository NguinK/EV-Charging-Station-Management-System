import React, { useEffect, useState } from "react";
import {
  Table,
  Card,
  Button,
  Tag,
  Space,
  message,
  Row,
  Col,
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
} from "antd";
import chargingStationAPI from "../../api/chargingStationAPI";
import chargingPointAPI from "../../api/chargingPointAPI";
import chargingSessionAPI from "../../api/chargingSessionAPI";

const ChargingStation = () => {
  const [stations, setStations] = useState([]);
  const [selectedStation, setSelectedStation] = useState(null);
  const [points, setPoints] = useState([]);
  const [loadingStations, setLoadingStations] = useState(false);
  const [loadingPoints, setLoadingPoints] = useState(false);
  const [openModal, setOpenModal] = useState(false);
  const [form] = Form.useForm();

  // ==============================
  // 🔄 FETCH API
  // ==============================
  const fetchStations = async () => {
    setLoadingStations(true);
    try {
      const res = await chargingStationAPI.getAllStations();
      setStations(res.data || []);
      if (res.data?.length > 0) setSelectedStation(res.data[0]);
    } catch (err) {
      console.error(err);
      message.error("Không thể tải danh sách trạm");
    } finally {
      setLoadingStations(false);
    }
  };

  const fetchPoints = async (stationId) => {
    setLoadingPoints(true);
    try {
      const res = await chargingPointAPI.getPointsByStation(stationId);
      setPoints(res.data || []);
    } catch (err) {
      message.error("Không thể tải trụ sạc",err);
    } finally {
      setLoadingPoints(false);
    }
  };

  useEffect(() => {
    fetchStations();
  }, []);

  useEffect(() => {
    if (selectedStation?.id) fetchPoints(selectedStation.id);
  }, [selectedStation]);

  // Refresh định kỳ mỗi 10s
  useEffect(() => {
    const interval = setInterval(() => {
      if (selectedStation?.id) fetchPoints(selectedStation.id);
    }, 10000);
    return () => clearInterval(interval);
  }, [selectedStation]);

  // ==============================
  // ➕ THÊM TRỤ SẠC
  // ==============================
  const handleAddPoint = async () => {
    try {
      const values = await form.validateFields();
      const payload = {
        stationId: selectedStation.id,
        pointCode: values.pointCode,
        connectorType: values.connectorType,
        maxPower: values.maxPower,
        pricePerKwh: values.pricePerKwh,
        pricePerMinute: values.pricePerMinute,
        status: "AVAILABLE",
      };
      await chargingPointAPI.createPoint(payload);
      message.success("Thêm trụ sạc thành công!");
      setOpenModal(false);
      form.resetFields();
      fetchPoints(selectedStation.id);
    } catch (err) {
      message.error("Không thể thêm trụ sạc!", err);
    }
  };

  // ==============================
  // 🗑️ XOÁ TRỤ SẠC
  // ==============================
  const handleDeletePoint = async (pointId) => {
    Modal.confirm({
      title: "Xác nhận xoá trụ sạc",
      content: "Bạn có chắc chắn muốn xoá trụ này không?",
      okText: "Xoá",
      okType: "danger",
      cancelText: "Huỷ",
      onOk: async () => {
        try {
          await chargingPointAPI.deletePoint(pointId);
          message.success("Đã xoá trụ sạc!");
          fetchPoints(selectedStation.id);
        } catch (err) {
          message.error("Lỗi khi xoá trụ sạc!", err);
        }
      },
    });
  };

  // ==============================
  // ⚡ SESSION HANDLERS
  // ==============================

  // 🚗 Bắt đầu sạc — cần reservationId (ví dụ từ DB booking)
  const handleStartCharging = async (reservationId) => {
    try {
      await chargingSessionAPI.startSession(reservationId);
      message.success("⚡ Bắt đầu sạc!");
      fetchPoints(selectedStation.id);
    } catch (err) {
      message.error("Không thể bắt đầu sạc!", err);
    }
  };

  // 🛑 Dừng sạc
  const handleStopCharging = async (sessionId) => {
    try {
      await chargingSessionAPI.endManualSession(sessionId);
      message.success("✅ Dừng sạc thành công!");
      fetchPoints(selectedStation.id);
    } catch (err) {
      message.error("Lỗi khi dừng sạc!", err);
    }
  };

  // 🔍 Kiểm tra trạng thái sạc
  const handleCheckStatus = async (sessionId) => {
    try {
      const res = await chargingSessionAPI.getSessionStatus(sessionId);
      Modal.info({
        title: "Trạng thái phiên sạc",
        content: (
          <p>
            Phiên sạc hiện tại:{" "}
            <strong>{res?.data?.status || "Không xác định"}</strong>
          </p>
        ),
      });
    } catch (err) {
      message.error("Không thể lấy trạng thái!",err);
    }
  };

  // ==============================
  // 🧾 CỘT DỮ LIỆU TRỤ
  // ==============================
  const pointColumns = [
    { title: "Mã trụ", dataIndex: "pointCode", key: "pointCode" },
    { title: "Loại sạc", dataIndex: "connectorType", key: "connectorType" },
    {
      title: "Giá tiền (₫/kWh)",
      dataIndex: "pricePerKwh",
      key: "pricePerKwh",
      render: (p) => (p ? p.toLocaleString("vi-VN") : "—"),
    },
    {
      title: "Công suất (kW)",
      dataIndex: "maxPower",
      key: "maxPower",
      render: (v) => (v ? `${v}` : "—"),
    },
    {
      title: "Trạng thái",
      dataIndex: "status",
      key: "status",
      render: (status) => (
        <Tag
          color={
            status === "AVAILABLE"
              ? "green"
              : status === "CHARGING"
              ? "blue"
              : status === "PENDING"
              ? "gold"
              : "red"
          }
        >
          {status}
        </Tag>
      ),
    },
    {
      title: "Hành động",
      key: "actions",
      render: (_, record) => (
        <Space>
          {record.status === "AVAILABLE" && (
            <Button
              type="primary"
              onClick={() => handleStartCharging(record.reservationId || 1)}
            >
              Bắt đầu
            </Button>
          )}
          {record.status === "CHARGING" && (
            <Button danger onClick={() => handleStopCharging(record.sessionId)}>
              Dừng
            </Button>
          )}
          {record.status === "PENDING" && (
            <Button onClick={() => handleCheckStatus(record.sessionId)}>
              Kiểm tra
            </Button>
          )}
          <Button danger onClick={() => handleDeletePoint(record.pointId)}>
            Xoá
          </Button>
        </Space>
      ),
    },
  ];

  // ==============================
  // ⚙️ GIAO DIỆN CHÍNH
  // ==============================
  return (
    <div className="p-6 min-h-screen bg-gray-50">
      <h1 className="text-2xl font-bold mb-2">⚡ Quản lý trạm sạc</h1>
      <Row gutter={20}>
        {/* ===== CỘT TRẠM BÊN TRÁI ===== */}
        <Col span={6}>
          <Card
            title="Danh sách trạm"
            className="shadow-md rounded-xl border border-gray-200"
            loading={loadingStations}
          >
            <Space direction="vertical" className="w-full">
              {stations.map((st) => (
                <Button
                  key={st.id}
                  type={selectedStation?.id === st.id ? "primary" : "default"}
                  block
                  onClick={() => setSelectedStation(st)}
                  className="flex justify-between items-center"
                >
                  <span>{st.name}</span>
                  <Tag
                    color={
                      st.status === "ONLINE"
                        ? "green"
                        : st.status === "OFFLINE"
                        ? "red"
                        : "gold"
                    }
                  >
                    {st.status}
                  </Tag>
                </Button>
              ))}
            </Space>
          </Card>
        </Col>

        {/* ===== CỘT PHẢI: DANH SÁCH TRỤ ===== */}
        <Col span={18}>
          <Card
            title={
              <Space>
                <span>Trụ sạc của trạm:</span>
                <strong>{selectedStation?.name || "—"}</strong>
              </Space>
            }
            extra={
              <Button type="primary" onClick={() => setOpenModal(true)}>
                + Thêm trụ
              </Button>
            }
            className="shadow-md rounded-xl border border-gray-200"
          >
            <Table
              rowKey="pointId"
              columns={pointColumns}
              dataSource={points}
              loading={loadingPoints}
              pagination={false}
            />
          </Card>
        </Col>
      </Row>

      {/* Modal thêm trụ */}
      <Modal
        title="Thêm trụ sạc mới"
        open={openModal}
        onCancel={() => setOpenModal(false)}
        onOk={handleAddPoint}
        okText="Thêm"
        cancelText="Huỷ"
      >
        <Form layout="vertical" form={form}>
          <Form.Item
            label="Mã trụ (pointCode)"
            name="pointCode"
            rules={[{ required: true, message: "Nhập mã trụ!" }]}
          >
            <Input placeholder="VD: CP001" />
          </Form.Item>

          <Form.Item
            label="Loại sạc"
            name="connectorType"
            rules={[{ required: true, message: "Chọn loại sạc!" }]}
          >
            <Select
              options={[
                { value: "CCS", label: "CCS" },
                { value: "CHAdeMO", label: "CHAdeMO" },
                { value: "AC Type 2", label: "AC Type 2" },
              ]}
            />
          </Form.Item>

          <Form.Item
            label="Công suất (kW)"
            name="maxPower"
            rules={[{ required: true, message: "Nhập công suất!" }]}
          >
            <InputNumber min={1} className="w-full" />
          </Form.Item>

          <Form.Item
            label="Giá mỗi kWh (₫)"
            name="pricePerKwh"
            rules={[{ required: true, message: "Nhập giá mỗi kWh!" }]}
          >
            <InputNumber min={0} step={100} className="w-full" />
          </Form.Item>

          <Form.Item
            label="Giá mỗi phút (₫)"
            name="pricePerMinute"
            rules={[{ required: true, message: "Nhập giá mỗi phút!" }]}
          >
            <InputNumber min={0} step={100} className="w-full" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ChargingStation;
