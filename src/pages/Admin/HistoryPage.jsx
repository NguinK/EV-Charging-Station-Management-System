import React from "react";
import {
  Card,
  Table,
  Button,
  DatePicker,
  Select,
  Input,
  Space,
  Tag,
} from "antd";

const HistoryPage = () => {
  return (
    <div className="w-full min-h-screen bg-white text-black px-6 py-6 space-y-8">

      {/* HEADER */}
      <div>
        <h1 className="text-3xl font-bold">History </h1>
        <p className="text-gray-600 text-sm mt-1">
          Theo dõi lịch sử sạc, doanh thu và thông tin người dùng theo ngày/tháng.
        </p>
      </div>

      {/* FILTER BAR */}
      <Card
        className="!bg-white shadow-md rounded-xl border border-gray-200"
        
      >
        <Space wrap size="middle">



          <DatePicker placeholder="Ngày" className="w-[140px]" />
          <DatePicker picker="month" placeholder="Tháng" className="w-[140px]" />

          <Select
            placeholder="Seat"
            style={{ width: 130 }}
            options={[{ value: "all", label: "Tất cả" }]}
          />

          <Select
            placeholder="Trạm"
            style={{ width: 130 }}
            options={[{ value: "all", label: "Tất cả" }]}
          />

          <Input placeholder="Tìm người dùng…" className="w-[180px]" />

          <Button type="primary" className="px-5">
            Áp dụng
          </Button>

          <Button className="px-5">⬇ Export CSV</Button>
        </Space>
      </Card>

      {/* ✅ STATS (4 ô NGANG TRÊN 1 HÀNG) */}
      <div className="grid grid-cols-4 gap-4">

        <Card className="shadow-md rounded-xl border border-gray-200">
          <p className="text-gray-600 text-sm">Lượt sạc</p>
          <h2 className="text-3xl font-bold mt-2">—</h2>
        </Card>

        <Card className="shadow-md rounded-xl border border-gray-200">
          <p className="text-gray-600 text-sm">Tổng điện (kWh)</p>
          <h2 className="text-3xl font-bold mt-2">—</h2>
        </Card>

        <Card className="shadow-md rounded-xl border border-gray-200">
          <p className="text-gray-600 text-sm">Doanh thu</p>
          <h2 className="text-3xl font-bold mt-2">—</h2>
        </Card>

        <Card className="shadow-md rounded-xl border border-gray-200">
          <p className="text-gray-600 text-sm">Thời lượng TB</p>
          <h2 className="text-3xl font-bold mt-2">—</h2>
        </Card>

      </div>

      {/* TABLE */}
      <Card className="shadow-md rounded-xl border border-gray-200">
        <Table
          dataSource={[]}
          columns={[
            { title: "Date", dataIndex: "date" },
            { title: "Time", dataIndex: "time" },
            { title: "User", dataIndex: "user" },
            { title: "Station", dataIndex: "station" },
            { title: "Port", dataIndex: "port" },
            { title: "Seat", dataIndex: "seat" },
            { title: "Duration", dataIndex: "duration" },
            { title: "Energy (kWh)", dataIndex: "energy" },
            { title: "Cost", dataIndex: "cost" },
            {
              title: "Status",
              dataIndex: "status",
              render: (s) =>
                s ? (
                  <Tag color={s === "Done" ? "green" : "red"}>{s}</Tag>
                ) : (
                  ""
                ),
            },
          ]}
          pagination={false}
        />
      </Card>
    </div>
  );
};

export default HistoryPage;
