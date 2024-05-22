let bird;
let pipes = [];
let birdImage;
let gameStarted = false;
let gameOver = false;
let score = 0;

function preload() {
  birdImage = loadImage('kisspng-314xelampaposs-profile-5c892a6a432231.390023621552493162275-removebg-preview.png'); // Load the bird image
}

function setup() {
  createCanvas(800, 600);
  bird = new Bird();
  pipes.push(new Pipe());
}

function draw() {
  background(135, 206, 235); // Cyan background

  if (gameStarted) {
    if (!gameOver) {
      bird.update();
      bird.show();

      if (frameCount % 100 == 0) {
        pipes.push(new Pipe());
      }

      for (let i = pipes.length - 1; i >= 0; i--) {
        pipes[i].update();
        pipes[i].show();

        if (pipes[i].hits(bird)) {
          gameOver = true;
        }

        if (pipes[i].offscreen()) {
          pipes.splice(i, 1);
          score++;
        }
      }
    } else {
      fill(255, 0, 0);
      textSize(32);
      text("Game Over", width / 2 - 70, height / 2);
      text("Press SPACE to restart", width / 2 - 120, height / 2 + 40);
    }
  } else {
    fill(0);
    textSize(32);
    text("Press SPACE to start", width / 2 - 130, height / 2);
  }

  fill(0);
  textSize(32);
  text("Score: " + score, 10, 30);
}

function keyPressed() {
  if (key == ' ') {
    if (gameOver) {
      resetGame();
    } else if (!gameStarted) {
      gameStarted = true;
    }
    bird.up();
  }
}

function resetGame() {
  bird = new Bird();
  pipes = [];
  pipes.push(new Pipe());
  score = 0;
  gameOver = false;
}

class Bird {
  constructor() {
    this.y = height / 2;
    this.x = 64;
    this.gravity = 0.6;
    this.lift = -15;
    this.velocity = 0;
  }

  show() {
    image(birdImage, this.x, this.y, 50, 50); // Draw bird image
  }

  up() {
    this.velocity += this.lift;
  }

  update() {
    this.velocity += this.gravity;
    this.y += this.velocity;

    if (this.y > height) {
      this.y = height;
      this.velocity = 0;
    }

    if (this.y < 0) {
      this.y = 0;
      this.velocity = 0;
    }
  }
}

class Pipe {
  constructor() {
    this.spacing = 175;
    this.top = random(height / 6, (3 / 4) * height);
    this.bottom = height - (this.top + this.spacing);
    this.x = width;
    this.w = 80;
    this.speed = 6;
    this.passed = false;
  }

  hits(bird) {
    if (bird.y < this.top || bird.y > height - this.bottom) {
      if (bird.x > this.x && bird.x < this.x + this.w) {
        return true;
      }
    }
    return false;
  }

  show() {
    fill(255);
    rect(this.x, 0, this.w, this.top);
    rect(this.x, height - this.bottom, this.w, this.bottom);
  }

  update() {
    this.x -= this.speed;
  }

  offscreen() {
    return this.x < -this.w;
  }
}