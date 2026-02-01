
import os
import random
from PIL import Image, ImageDraw, ImageFont

def create_mockup(screen_type, filename, theme_color="#3D5AFE", bg_color="#121212"):
    width, height = 1080, 1920
    img = Image.new('RGB', (width, height), bg_color)
    d = ImageDraw.Draw(img)
    
    # Status Bar
    d.rectangle([0, 0, width, 60], fill="#000000")
    
    # App Bar
    d.rectangle([0, 60, width, 200], fill=bg_color)
    d.text((50, 100), screen_type, fill="white", font_size=60) # Simple text if font fails
    
    surface_color = "#1E1E1E"
    text_primary = "#FFFFFF"
    text_secondary = "#AAAAAA"
    
    if screen_type == "Recordings":
        # Draw List Items
        for i in range(5):
            y = 220 + i * 250
            # Card
            d.rectangle([40, y, width-40, y+220], fill=surface_color, outline=None)
            
            # Avatar Circle
            d.ellipse([70, y+35, 70+150, y+35+150], fill="#2C2C2C")
            # Icon inside
            d.rectangle([110, y+90, 180, y+130], fill=theme_color if i%2==0 else "#03DAC6")
            
            # Text Lines
            d.rectangle([250, y+50, 700, y+90], fill=text_primary) # Name
            d.rectangle([250, y+110, 500, y+140], fill=text_secondary) # Number/Time
            d.rectangle([width-200, y+50, width-80, y+90], fill=text_secondary) # Duration

    elif screen_type == "Details":
        # Large Avatar
        d.ellipse([width//2 - 150, 250, width//2 + 150, 550], fill="#2C2C2C")
        d.rectangle([width//2 - 50, 350, width//2 + 50, 450], fill=theme_color)
        
        # Name & Number
        d.rectangle([width//2 - 200, 600, width//2 + 200, 660], fill=text_primary)
        d.rectangle([width//2 - 150, 680, width//2 + 150, 720], fill=text_secondary)
        
        # Waveform card
        card_y = 800
        d.rectangle([40, card_y, width-40, card_y+400], fill=surface_color)
        
        # Waveform bars
        for i in range(50, 950, 20):
            h = random.randint(20, 150)
            d.rectangle([i+40+10, card_y+200-h, i+40+20, card_y+200+h], fill=theme_color)
            
        # Play button
        d.ellipse([width//2 - 60, card_y+280, width//2 + 60, card_y+400], fill="white")
        d.polygon([(width//2 - 15, card_y+320), (width//2 - 15, card_y+360), (width//2 + 25, card_y+340)], fill=theme_color)

        # Metadata
        meta_y = 1250
        d.rectangle([40, meta_y, width-40, meta_y+400], fill=surface_color)
        for i in range(4):
            line_y = meta_y + 40 + i*90
            d.rectangle([80, line_y, 400, line_y+30], fill=text_secondary)
            d.rectangle([width-400, line_y, width-80, line_y+30], fill=text_primary)

    elif screen_type == "Settings":
        # Sections
        sections = ["Recording", "Backup", "Security", "Storage", "About"]
        current_y = 220
        
        for section in sections:
            # Section Header
            d.rectangle([60, current_y, 400, current_y+40], fill=theme_color)
            current_y += 60
            
            # Card
            d.rectangle([40, current_y, width-40, current_y+200], fill=surface_color)
            
            # Toggle item 1
            d.rectangle([80, current_y+40, 600, current_y+80], fill=text_primary)
            d.rectangle([80, current_y+90, 400, current_y+120], fill=text_secondary)
            
            # Switch
            switch_x = width - 180
            d.rectangle([switch_x, current_y+60, switch_x+100, current_y+100], fill=theme_color)
            d.ellipse([switch_x+60, current_y+60, switch_x+100, current_y+100], fill="white")
            
            current_y += 240

    elif screen_type == "AppLock":
        # Dark Overlay
        d.rectangle([0, 0, width, height], fill="black")
        
        # Biometric Prompt
        prompt_w, prompt_h = 800, 500
        prompt_x = (width - prompt_w) // 2
        prompt_y = (height - prompt_h) // 2
        
        d.rectangle([prompt_x, prompt_y, prompt_x+prompt_w, prompt_y+prompt_h], fill=surface_color)
        
        # Fingerprint Icon
        icon_size = 120
        d.ellipse([width//2 - icon_size//2, prompt_y + 80, width//2 + icon_size//2, prompt_y + 80 + icon_size], fill=theme_color)
        
        # Text
        d.rectangle([width//2 - 200, prompt_y + 250, width//2 + 200, prompt_y + 290], fill=text_primary)
        d.rectangle([width//2 - 150, prompt_y + 310, width//2 + 150, prompt_y + 340], fill=text_secondary)
        
        # Cancel Button
        d.rectangle([prompt_x + 50, prompt_y + 400, prompt_x + 200, prompt_y + 440], fill=text_secondary)

    elif screen_type == "EmptyState":
        # Empty illustration
        center_y = height // 2 - 100
        d.ellipse([width//2 - 200, center_y - 200, width//2 + 200, center_y + 200], fill="#2C2C2C")
        d.rectangle([width//2 - 100, center_y - 100, width//2 + 100, center_y + 100], fill="#333333")
        
        # Empty text
        d.rectangle([width//2 - 250, center_y + 250, width//2 + 250, center_y + 290], fill=text_primary)
        d.rectangle([width//2 - 180, center_y + 310, width//2 + 180, center_y + 340], fill=text_secondary)

    # Save
    os.makedirs(os.path.dirname(filename), exist_ok=True)
    img.save(filename)
    print(f"Created {filename}")

create_mockup("Recordings", "screenshots/recordings_list.png")
create_mockup("Details", "screenshots/recording_details.png")
create_mockup("Settings", "screenshots/settings.png")
create_mockup("AppLock", "screenshots/app_lock.png")
create_mockup("EmptyState", "screenshots/empty_state.png")
