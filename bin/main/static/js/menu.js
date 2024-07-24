
// 상단 메뉴 바
const navbar = document.getElementById("navbar");
const menu = document.getElementById("menu");
const menuChildren = menu.children;

navbar.addEventListener("mouseover", () => {
    for(let i = 0; i < menuChildren.length; i++) {
        menuChildren[i].style.display = "block";
    }
});

navbar.addEventListener("mouseout", () => {
    for(let i = 0; i < menuChildren.length; i++) {
        menuChildren[i].style.display = "none";
    }
})

