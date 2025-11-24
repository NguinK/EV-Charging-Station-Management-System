import React, { useEffect, useState } from "react";
import {
  Table,
  Card,
  Button,
  Tag,
  Space,
  Row,
  Col,
  Modal as AntModal,
  Form,
  Input,
  InputNumber,
  Select,
  Modal,
} from "antd";

import chargingStationAPI from "../../api/chargingStationAPI";
import chargingPointAPI from "../../api/chargingPointAPI";
import chargingSessionAPI from "../../api/chargingSessionAPI";
import { App } from "antd";

const ChargingStation = () => {
  const [stations, setStations] = useState([]);
  const [selectedStation, setSelectedStation] = useState(null);
  const [points, setPoints] = useState([]);
  const [loadingStations, setLoadingStations] = useState(false);
  const [loadingPoints, setLoadingPoints] = useState(false);
  const [openModal, setOpenModal] = useState(false);
  const [form] = Form.useForm();
  const { modal, message } = App.useApp();

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
      message.error("Không thể tải trụ sạc", err);
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
    modal.confirm({
      title: "Xác nhận xoá trụ sạc",
      content: "Bạn có chắc chắn muốn xoá trụ này không?",
      okText: "Xoá",
      okType: "danger",
      cancelText: "Huỷ",
      onOk: async () => {
        try {
          await chargingPointAPI.deletePoint(pointId);
          message.success("Đã xoá trụ sạc!");
          await fetchPoints(selectedStation.id);
        } catch (err) {
          message.error("Lỗi khi xoá trụ sạc!", err);
        }
      },
    });
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
      message.error("Không thể lấy trạng thái!", err);
    }
  };

  // ==============================
  // 🧾 CỘT DỮ LIỆU TRỤ
  // ==============================
  const pointColumns = [
    { title: "Point Code", dataIndex: "pointCode", key: "pointCode" },
    {
      title: "Conector Type",
      dataIndex: "connectorType",
      key: "connectorType",
    },
    {
      title: "Per Kwh (₫/kWh)",
      dataIndex: "pricePerKwh",
      key: "pricePerKwh",
      render: (p) => (p ? p.toLocaleString("vi-VN") : "—"),
    },
    {
      title: "Per Minute (₫/min)",
      dataIndex: "pricePerMinute",
      key: "pricePerMinute",
      render: (p) => (p ? p.toLocaleString("vi-VN") : "—"),
    },
    {
      title: "Max Power (kW)",
      dataIndex: "maxPower",
      key: "maxPower",
      render: (v) => (v ? `${v}` : "—"),
    },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
      render: (status) => (
        <Tag
          color={
            status === "AVAILABLE"
              ? "green"
              : status === "OCCUPIED"
              ? "blue"
              : status === "RESERVED"
              ? "red"
              : "gold"
          }
        >
          {status}
        </Tag>
      ),
    },
    {
      title: "Actions",
      key: "actions",
      render: (_, record) => (
        <Space>
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
          <Button danger onClick={() => handleDeletePoint(record.id)}>
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
      <h1 className="text-2xl font-bold mb-2">⚡ Station Management</h1>
      <Row gutter={20}>
        {/* ===== CỘT TRẠM BÊN TRÁI ===== */}
        <Col span={6}>
          <Card
            title="Station List"
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
                      st.status === "ACTIVE"
                        ? "green"
                        : st.status === "INACTIVE"
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
                <span> Charging points of station:</span>
                <strong>{selectedStation?.name || "—"}</strong>
              </Space>
            }
            extra={
              <Button type="primary" onClick={() => setOpenModal(true)}>
                + Add Point
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
      <AntModal
        title="Add Charging Point"
        open={openModal}
        onCancel={() => setOpenModal(false)}
        onOk={handleAddPoint}
        okText="Thêm"
        cancelText="Huỷ"
      >
        <Form layout="vertical" form={form}>
          <Form.Item label="Point Code" name="pointCode">
            <Input placeholder="VD: A1" />
          </Form.Item>

          <Form.Item
            label="Connector Type"
            name="connectorType"
            rules={[{ required: true, message: "Select Connector Type" }]}
          >
            <Select
              options={[
                { value: "CCS", label: "CCS" },
                { value: "CHADEMO", label: "CHADEMO" },
                { value: "AC", label: "AC" },
              ]}
            />
          </Form.Item>

          <Form.Item
            label="Max Power (kW)"
            name="maxPower"
            rules={[
              { required: true, message: "Input Power" },
              { type: "number", min: 100, message: "Values must be > 0" },
            ]}
          >
            <InputNumber min={1} className="w-full" />
          </Form.Item>

          <Form.Item
            label="Price per kWh (₫)"
            name="pricePerKwh"
            rules={[
              { required: true, message: "Please input price per kWh" },
              { type: "number", min: 1000, message: "Values must be > 1000" },
            ]}
          >
            <InputNumber min={0} step={100} className="w-full" />
          </Form.Item>

          <Form.Item
            label="Price per minute (₫)"
            name="pricePerMinute"
            rules={[
              { required: true, message: "Please input price per minute" },
              { type: "number", min: 1000, message: "Values must be > 1000" },
            ]}
          >
            <InputNumber min={0} step={100} className="w-full" />
          </Form.Item>
        </Form>
      </AntModal>
    </div>
  );
};

export default ChargingStation;
