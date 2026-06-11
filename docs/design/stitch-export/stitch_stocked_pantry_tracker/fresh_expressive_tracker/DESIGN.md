---
name: Fresh Expressive Tracker
colors:
  surface: '#f7fbf2'
  surface-dim: '#d7dbd3'
  surface-bright: '#f7fbf2'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f1f5ec'
  surface-container: '#ebefe6'
  surface-container-high: '#e5eae1'
  surface-container-highest: '#e0e4db'
  on-surface: '#181d18'
  on-surface-variant: '#3e4a3d'
  inverse-surface: '#2d322c'
  inverse-on-surface: '#eef2e9'
  outline: '#6e7a6c'
  outline-variant: '#bdcab9'
  surface-tint: '#006e2a'
  primary: '#006b29'
  on-primary: '#ffffff'
  primary-container: '#008736'
  on-primary-container: '#f7fff2'
  inverse-primary: '#67de7c'
  secondary: '#2b6c00'
  on-secondary: '#ffffff'
  secondary-container: '#a8f37d'
  on-secondary-container: '#2f7103'
  tertiary: '#765700'
  on-tertiary: '#ffffff'
  tertiary-container: '#956e00'
  on-tertiary-container: '#fffbff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#84fb95'
  primary-fixed-dim: '#67de7c'
  on-primary-fixed: '#002108'
  on-primary-fixed-variant: '#00531e'
  secondary-fixed: '#abf680'
  secondary-fixed-dim: '#90d967'
  on-secondary-fixed: '#082100'
  on-secondary-fixed-variant: '#1f5100'
  tertiary-fixed: '#ffdfa0'
  tertiary-fixed-dim: '#fbbc00'
  on-tertiary-fixed: '#261a00'
  on-tertiary-fixed-variant: '#5c4300'
  background: '#f7fbf2'
  on-background: '#181d18'
  surface-variant: '#e0e4db'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 57px
    fontWeight: '700'
    lineHeight: 64px
    letterSpacing: -0.25px
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
  title-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Be Vietnam Pro
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.5px
  body-md:
    fontFamily: Be Vietnam Pro
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.25px
  label-lg:
    fontFamily: Be Vietnam Pro
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-sm:
    fontFamily: Be Vietnam Pro
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  base: 8px
  margin-mobile: 16px
  margin-tablet: 24px
  margin-desktop: 32px
  gutter: 16px
  stack-sm: 4px
  stack-md: 12px
  stack-lg: 24px
---

## Brand & Style
The design system for this application is built on a foundation of vitality, efficiency, and clarity. It targets busy households and eco-conscious individuals who want to reduce food waste through proactive management. The brand personality is "The Helpful Gardener"—knowledgeable, encouraging, and vibrant.

The visual style follows a **Modern Material** approach with an **Expressive** tilt. It leverages the native Android "Edge-to-Edge" philosophy, utilizing expansive whitespace and high-energy organic shapes to make the mundane task of inventory management feel delightful. The emotional response should be one of "controlled freshness"—users should feel organized and relieved, never overwhelmed by the data of their pantry.

## Colors
The palette is rooted in a "Fresh Green" primary (#00933B) that signals growth and health. This system is designed to be **dynamic-color friendly**, allowing the primary seed to influence the tonal palettes of the entire UI.

- **Primary:** Used for key actions (Add Item) and active states.
- **Secondary:** A softer lime-green used for category chips and tonal backgrounds.
- **Tertiary (Amber):** Specifically reserved for "Near Expiry" warnings (e.g., 2-3 days remaining).
- **Error (Red):** Used exclusively for "Expired" items and critical deletion actions.
- **Neutral:** A slightly "minted" off-white (#F7FBF2) for surfaces to maintain the fresh aesthetic without the harshness of pure white.

## Typography
This design system utilizes **Plus Jakarta Sans** for headings to provide a friendly, rounded, and expressive character that complements the organic UI shapes. **Be Vietnam Pro** is used for body text and labels to ensure high legibility and a contemporary, professional feel.

Hierarchy is established through significant weight variance. Headlines are bold and tight to feel "impactful," while body copy uses generous line heights to ensure the interface feels airy and readable even when listing dozens of grocery items.

## Layout & Spacing
This system follows an **8px grid** and a **Fluid-Edge layout**. Content should flow edge-to-edge on mobile devices, with critical UI elements respecting a 16px side margin.

- **Mobile (0-599dp):** Single column, 16px margins. Bottom App Bar for primary navigation.
- **Tablet (600-839dp):** 8-column grid, 24px margins. Content cards may span multiple columns. Navigation Rail on the left.
- **Desktop (840dp+):** 12-column grid, 32px margins. Fixed Navigation Drawer.

Vertical spacing (stacking) is generous to maintain the "playful but clean" aesthetic. Grouped items (like items in a specific fridge shelf) use `stack-md`, while major sections use `stack-lg`.

## Elevation & Depth
In line with Material 3 principles, depth is primarily communicated through **Tonal Elevation** rather than heavy shadows.

- **Level 0 (Surface):** The lowest layer, using the neutral background color.
- **Level 1 (Cards):** Low-contrast surfaces that use a subtle primary container tint.
- **Level 2 (Floating Action Buttons):** These use soft, diffused "ambient" shadows with a slight tint of the primary green to appear "lifted" and interactive.
- **Backdrop Blurs:** Used sparingly on top app bars during scroll to maintain a sense of context with the content passing underneath.

## Shapes
The shape language is the core of this design system's "Expressive" identity. It utilizes a **Pill-shaped (Level 3)** roundedness philosophy.

- **Buttons & Chips:** Always fully rounded (pill-shaped).
- **Cards:** Use an extra-large 28px corner radius (`rounded-xl` equivalent) to feel soft and approachable.
- **Input Fields:** Semi-rounded (16px) to maintain a structural feel while still fitting the soft aesthetic.
- **Bottom Sheets:** 32px top corner radius to emphasize the "tactile" nature of the sheet being pulled up.

## Components

### Buttons
Primary buttons are pill-shaped, using high-contrast primary colors with white text. Secondary buttons use a tonal container (light green background with dark green text).

### Grocery Cards
Cards are the primary data containers. They use a Level 1 tonal elevation. The "Expiry Indicator" is a vertical 8px pill on the left edge of the card, color-coded by urgency (Green, Amber, Red).

### Chips (Category Filters)
Chips are used for filtering (e.g., "Fridge", "Pantry", "Dairy"). They utilize a pill shape with a 1px stroke when inactive, and a solid tonal fill when active.

### Input Fields
Filled text fields with a soft 16px top-rounded corner. The indicator line is replaced by a subtle background color shift on focus to keep the UI clean.

### FAB (Floating Action Button)
The "Add Item" FAB is a large, rounded-square (M3 style) or pill, anchored to the bottom right, utilizing the most vibrant primary green to ensure it is the focal point of the screen.

### Progress Bars (Freshness Meter)
A custom thick, rounded progress bar (12px height) used within cards to show the percentage of "life" remaining for an item, transitioning from Green to Red.