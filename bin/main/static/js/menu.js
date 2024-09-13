
// 내비바 컨트롤
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
            item.querySelector('div').addEventListener('click', () => {
                dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
            }); 
        }
    });
});

// 햄버거 버튼 컨트롤
function toggleMenu() {
    const navbar = document.getElementById('navbar');
    navbar.classList.toggle('active');
}

// 화면의 다른 곳을 클릭하면 메뉴를 닫음
document.addEventListener('click', function(event) {
    const navbar = document.getElementById('navbar');
    const hamburger = document.querySelector('.hamburger');

    // 햄버거 버튼이나 메뉴 내부를 클릭한 것이 아니면 메뉴를 닫음
    if (!navbar.contains(event.target) && !hamburger.contains(event.target)) {
        navbar.classList.remove('active');
    }
});