-- Thêm rào chắn chống Race Condition Database-level bằng Partial Unique Index

-- Chống tạo 2 hold cùng lúc trên 1 ghế đang ở trạng thái HOLDING
CREATE UNIQUE INDEX IF NOT EXISTS idx_seat_holds_holding 
ON seat_holds (trip_id, seat_number) 
WHERE hold_status = 'HOLDING';

-- Chống bán 2 vé trên cùng 1 ghế đang ở trạng thái ACTIVE
CREATE UNIQUE INDEX IF NOT EXISTS idx_tickets_active 
ON tickets (trip_id, seat_number) 
WHERE ticket_status = 'ACTIVE';
