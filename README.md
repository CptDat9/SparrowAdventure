# Sparrow Adventure

<p align="center">
  <a href="https://git.io/typing-svg">
    <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&pause=1000&center=true&vCenter=true&random=false&width=450&lines=Sparrow+Adventure" alt="Typing SVG" />
  </a>
</p>

<div align="center">
  <img alt="Course Badge" src="https://img.shields.io/badge/HUST-course-blue?labelColor=EE4E4E&color=151515">
  <img alt="Code Size" src="https://img.shields.io/github/languages/code-size/ppap-1264589/SparrowAdventure?labelColor=7AA2E3&color=97E7E1">
</div>

---

## Mục lục

- [Giới thiệu](#giới-thiệu)
- [Thành viên](#thành-viên)
- [Tính năng](#tính-năng)
- [Cài đặt và triển khai](#cài-đặt-và-triển-khai)
- [Minh họa](#minh-họa)

---

## Giới thiệu

 **Sparrow Adventure** là một game RPG được phát triển trong bài tập lớn OOP của nhóm **06** thuộc lớp của thầy Trần Nhật Hóa, ngôn ngữ chính sử dụng Java. Trong game, người chơi điều khiển Captain Jack Sparrow để khám phá các vùng đất, chiến đấu với kẻ thù và tìm kiếm kho báu Aztec. Game cung cấp các tính năng như di chuyển, tấn công, thu thập vật phẩm, quản lý kho đồ, và hệ thống tính điểm dựa trên số lượng kẻ thù bị hạ và kho báu thu thập được.
 
### Điểm nổi bật:
- Hệ thống gameplay đa dạng: di chuyển, tấn công, nhảy, và thu thập vật phẩm.
- Hệ thống điểm số dựa trên số lượng kẻ địch bị tiêu diệt và kho báu thu thập.
- Các màn chơi được thiết kế phong phú và giao diện trực quan.

---

## Thành viên

Nhóm phát triển bao gồm:
- **Đỗ Xuân Hoàng**
- **Đoàn Xuân Công Đạt**
- **Nguyễn Tùng Dương**
- **Nguyễn Trung Hiếu**
- **Nguyễn Hoài Phương**

---

## Tính năng

### 1. Người chơi

#### Di chuyển:
- Người chơi có thể:
  - Sang trái: **A**
  - Sang phải: **D**
  - Nhảy: **Space** (Nhảy cao với **Double Space**).
  - Tấn công: **Chuột trái** (vung kiếm), **Chuột phải** (tấn công + di chuyển nhanh).

<div align="center">
  <img src="https://github.com/user-attachments/assets/8d3b8f0a-b168-4567-acec-99c85e691b34" alt="Player Movement">
</div>

#### Chọn nhân vật:
- Người chơi có thể chọn một trong **3 loại nhân vật** khi khởi động trò chơi.
- Vị trí và hoạt ảnh của các nhân vật được thiết kế trong lớp `PlayerSelection()`.

<div align="center">
  <img src="https://github.com/user-attachments/assets/03ede0df-25ba-4532-bb44-41549ec0371a" alt="Player Selection">
</div>

#### Tấn công:
- Người chơi được trang bị **kiếm** để tấn công kẻ địch trong phạm vi.
- Cách tấn công:
  - **Chuột trái**: Vung kiếm để gây sát thương.

<div align="center">
  <img src="https://github.com/user-attachments/assets/692ed80f-881d-4a07-893e-551f2d7eced9" alt="Player Attack">
</div>

---

### 2. Quái vật

- Quái vật (Enemy):
  - Có khả năng **tấn công tự động**.
  - Nhận diện và đuổi theo người chơi trong một **bán kính nhất định**.
  - Tấn công gây sát thương từ cả hai phía (trái/phải).
- Thiết kế lớp quái vật:
  - Sử dụng kế thừa từ lớp trừu tượng `Entity`.
  - Các lớp cụ thể: `Crabby`, `Shark`.

<div align="center">
  <img src="https://github.com/user-attachments/assets/1fac4bc8-99a2-4179-a061-9bcd418a45db" alt="Enemy Mechanics">
</div>

---

### 3. Điều chỉnh âm thanh

- Âm thanh trong game có thể được điều chỉnh để nâng cao trải nghiệm người chơi.

<div align="center">
  <img src="https://github.com/user-attachments/assets/2ff81427-64fb-451d-9aa5-592b641d6af5" alt="Sound Adjustment">
</div>

---

### 4. Màn hình chuyển tiếp và Credits

#### Màn hình chuyển tiếp:
- Hiển thị sau khi người chơi thua hoặc hoàn thành một cấp độ.

<div align="center">
  <img src="https://github.com/user-attachments/assets/6ddd6fdd-ef16-48af-b381-69dc7365f5bb" alt="Transition Screen">
</div>

#### Credits:
- Hiển thị danh sách nhóm phát triển và đóng góp.

<div align="center">
  <img src="https://github.com/user-attachments/assets/95870c65-0817-4e81-9b54-a616b1596b38" alt="Credits">
</div>

---

## Cài đặt và triển khai

- **IDE**: `Eclipse`
- **Ngôn ngữ lập trình**: `Java`

---

## Minh họa

- Xem video minh họa gameplay tại: [YouTube](https://www.youtube.com/watch?v=DIGRsG6QCTc)
