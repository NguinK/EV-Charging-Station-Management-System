import React, { useEffect, useState } from "react";
import { Table, Card, Button, Tag, Space, message, Row, Col } from "antd";
import chargingStationAPI from "../../api/chargingStationAPI";
import chargingPointAPI from "../../api/chargingPointAPI";
import chargingSessionAPI from "../../api/chargingSessionAPI";

const ChargingStation = () => {
  const [stations, setStations] = useState([]);
  const [selectedStation, setSelectedStation] = useState(null);
  const [points, setPoints] = useState([]);
  const [loadingStations, setLoadingStations] = useState(false);
  const [loadingPoints, setLoadingPoints] = useState(false);

  // 🧭 Lấy danh sách trạm
  const fetchStations = async () => {
    setLoadingStations(true);
    try {
      const res = await chargingStationAPI.getAllStations();
      setStations(res.data || []);
      if (res.data?.length > 0) setSelectedStation(res.data[0]); // chọn trạm đầu tiên mặc định
    } catch (err) {
      console.error(err);
      message.error("Không thể tải danh sách trạm");
    } finally {
      setLoadingStations(false);
    }
  };

  // ⚙️ Lấy danh sách trụ sạc theo trạm
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

  // Khi đổi trạm
  useEffect(() => {
    if (selectedStation?.id) {
      fetchPoints(selectedStation.id);
    }
  }, [selectedStation]);

  // 🚗 Bắt đầu sạc
  const handleStartCharging = async (pointId) => {
    try {
      await chargingSessionAPI.startSession(pointId, 1); // tạm userId=1
      message.success("⚡ Bắt đầu sạc!");
      fetchPoints(selectedStation.id);
    } catch (err) {
      message.error("Lỗi khi bắt đầu sạc",err);
    }
  };

  // 🛑 Dừng sạc
  const handleStopCharging = async (sessionId) => {
    try {
      await chargingSessionAPI.endSession(sessionId);
      message.success("✅ Dừng sạc thành công!");
      fetchPoints(selectedStation.id);
    } catch (err) {
      message.error("Lỗi khi dừng sạc",err);
    }
  };

  // 📊 Cột bảng trụ sạc
  const pointColumns = [
    { title: "Tên trụ sạc", dataIndex: "pointCode", key: "pointCode" },
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
              onClick={() => handleStartCharging(record.pointId)}
            >
              Bắt đầu
            </Button>
          )}
          {record.status === "CHARGING" && (
            <Button
              danger
              onClick={() => handleStopCharging(record.sessionId)}
            >
              Dừng
            </Button>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div className="p-6 min-h-screen bg-gray-50">
      <h1 className="text-2xl font-bold mb-2">⚡ Quản lý trạm sạc</h1>
      <Row gutter={20}>
        {/* ===== CỘT TRÁM SẠC BÊN TRÁI ===== */}
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
                    className="ml-2"
                  >
                    {st.status}
                  </Tag>
                </Button>
              ))}
            </Space>
          </Card>
        </Col>

        {/* ===== CỘT PHẢI: DANH SÁCH TRỤ CỦA TRẠM ===== */}
        <Col span={18}>
          <Card
            title={
              <Space>
                <span>Trụ sạc của trạm:</span>
                <strong>{selectedStation?.name || "—"}</strong>
              </Space>
            }
            extra={
              <Button type="primary" onClick={() => message.info("Thêm trụ mới")}>
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
    </div>
  );
};

export default ChargingStation;
