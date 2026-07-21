document.addEventListener("DOMContentLoaded", () => {
    const card = document.querySelector('.glass-card');

    // 3D Tilt Effect on hover
    document.addEventListener('mousemove', (e) => {
        if (window.innerWidth <= 768) return; // disable on mobile
        
        const xAxis = (window.innerWidth / 2 - e.pageX) / 50;
        const yAxis = (window.innerHeight / 2 - e.pageY) / 50;
        
        // We only want the effect if the user is hovering over or near the card
        // This makes the whole hero section responsive to mouse movement
        if(card) {
            card.style.transform = `perspective(1000px) rotateY(${xAxis}deg) rotateX(${yAxis}deg)`;
        }
    });
});
