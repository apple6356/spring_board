
// 내비 바 컨트롤
document.addEventListener('DOMContentLoaded', () => {
    const menuItems = document.querySelectorAll('#menu > li');

    menuItems.forEach(item => {
        const dropdown = item.querySelector('.dropdown');

        if (dropdown) {
            item.addEventListener('mouseenter', () => {
                dropdown.style.display = 'block';  // 드롭다운 표시
                dropdown.style.backgroundColor = '#fff';  // 배경색 설정
                dropdown.style.padding = '10px';  // 패딩 설정
            });

            item.addEventListener('mouseleave', () => {
                dropdown.style.display = 'none';  // 드롭다운 숨김
            });

            // 터치 디바이스를 위한 클릭 이벤트
            // item.querySelector('span').addEventListener('click', () => {
            //     dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
            // }); 
        }
    });
});