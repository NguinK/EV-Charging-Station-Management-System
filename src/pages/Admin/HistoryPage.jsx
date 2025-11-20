
import React, { useState } from "react";
import {
  Card,
  Button,
  DatePicker,
  Select,
  Space,
  message,
} from "antd";
import axios from "axios";

const { RangePicker } = DatePicker;

const HistoryPage = () => {
  // khoảng thời gian chọn trong DatePicker
  const [range, setRange] = useState([]);
  // danh sách trạm nhận từ API
  const [stations, setStations] = useState([]);
  // trạm đang chọn
  const [selectedStation, setSelectedStation] = useState("all");
  // số liệu hiển thị 4 ô
  const [stats, setStats] = useState({
    totalSessions: 0,
    totalEnergy: 0,
    totalRevenue: 0,
    avgDuration: 0,
  });

  const formatNumber = (n) =>
    (n ?? 0).toLocaleString("vi-VN", {
     minimumFractionDigits: 0,
    maximumFractionDigits: 0,
    });

  const handleApply = async () => {
    if (!range || range.length !== 2) {
      return message.warning("Vui lòng chọn khoảng thời gian");
    }

    const [start, end] = range;

    // format đúng kiểu backend: 2025-11-19T00:00:00+07:00
    const startDate = start
      .startOf("day")
      .format("YYYY-MM-DD[T]HH:mm:ssZ");
    const endDate = end
      .endOf("day")
      .format("YYYY-MM-DD[T]HH:mm:ssZ");

    const token = localStorage.getItem("token");

    try {
      const res = await axios.get(
        "http://localhost:8080/api/admin/reports/revenue/by-station",
        {
          params: {
            startDate,
            endDate,
          },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const data = res.data || [];

      // tạo options cho Select trạm
      const stationOptionsFromApi = data.map((s) => ({
        value: s.stationId,
        label: s.stationName,
      }));
      setStations(stationOptionsFromApi);

      // chọn dữ liệu để hiển thị
      let result;

      if (selectedStation === "all") {
        // cộng tổng tất cả trạm
        const totalSessions = data.reduce(
          (sum, x) => sum + (x.totalSessions || 0),
          0
        );
        const totalEnergy = data.reduce(
          (sum, x) => sum + (x.totalEnergy || 0),
          0
        );
        const totalRevenue = data.reduce(
          (sum, x) => sum + (x.totalRevenue || 0),
          0
        );

        // nếu backend trả averageDuration thì dùng, không có thì 0
        const avgDuration =
          data.length > 0
            ? data.reduce(
                (sum, x) => sum + (x.averageDuration || 0),
                0
              ) / data.length
            : 0;

        result = {
          totalSessions,
          totalEnergy,
          totalRevenue,
          avgDuration,
        };
      } else {
        // chỉ lấy 1 trạm theo stationId
        result =
          data.find((x) => x.stationId === selectedStation) || {};
      }

      setStats({
        totalSessions: result.totalSessions || 0,
        totalEnergy: result.totalEnergy || 0,
        totalRevenue: result.totalRevenue || 0,
        // nếu backend trả averageRevenuePerSession hoặc averageDuration
        avgDuration:
          result.avgDuration ||
          result.averageDuration ||
          result.averageRevenuePerSession ||
          0,
      });
    } catch (error) {
      console.error(error);
      message.error("Không thể tải báo cáo doanh thu");
    }
  };

  return (
    <div className="w-full min-h-screen bg-white text-black px-6 py-6 space-y-8">
      {/* HEADER */}
      <div>
        <h1 className="text-3xl font-bold">📊Revenue </h1>
      </div>

      {/* FILTER BAR */}
      <Card className="!bg-white shadow-md rounded-xl border border-gray-200">
        <Space wrap size="middle">
          {/* Chọn khoảng thời gian */}
          <RangePicker
            showTime={false}
            onChange={(values) => setRange(values || [])}
            className="w-[260px]"
            placeholder={["Từ ngày", "Đến ngày"]}
          />

          {/* Chọn trạm */}
          <Select
            placeholder="Trạm"
            style={{ width: 180 }}
            value={selectedStation}
            onChange={(value) => setSelectedStation(value)}
            options={[
              { value: "all", label: "Tất cả" },
              ...stations,
            ]}
          />

          <Button
            type="primary"
            className="px-5"
            onClick={handleApply}
          >
            Áp dụng
          </Button>
        </Space>
      </Card>

      {/* STATS */}
      <div className="grid grid-cols-4 gap-4">
        <Card className="shadow-md rounded-xl border border-gray-200">
          <p className="text-gray-600 text-sm">Charging session</p>
          <h2 className="text-3xl font-bold mt-2">
            {formatNumber(stats.totalSessions)}
          </h2>
        </Card>

        <Card className="shadow-md rounded-xl border border-gray-200">
          <p className="text-gray-600 text-sm">Total Energy (kWh)</p>
          <h2 className="text-3xl font-bold mt-2">
            {formatNumber(stats.totalEnergy)}
          </h2>
        </Card>

        <Card className="shadow-md rounded-xl border border-gray-200">
          <p className="text-gray-600 text-sm">Revenue</p>
          <h2 className="text-3xl font-bold mt-2">
            {formatNumber(stats.totalRevenue)} đ
          </h2>
        </Card>

        <Card className="shadow-md rounded-xl border border-gray-200">
          <p className="text-gray-600 text-sm">Average for Session </p>
          <h2 className="text-3xl font-bold mt-2">
            {formatNumber(stats.avgDuration)} đ
          </h2>
        </Card>
      </div>
    </div>
  );
};

export default HistoryPage;
