package tcb.adventurousdungeons.api.script.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import tcb.adventurousdungeons.api.script.IDungeonScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponentCreationGuiFactory;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Port;
import tcb.adventurousdungeons.client.gui.GuiEditScript;
import tcb.adventurousdungeons.registries.ScriptComponentRegistry;
import tcb.adventurousdungeons.util.CatmullRomSpline;
import tcb.adventurousdungeons.util.text.TextContainer;
import tcb.adventurousdungeons.util.text.TextContainer.TextPage;

public class GuiScriptComponent extends Gui {
	private IDungeonScriptComponent component;
	private GuiEditScript gui;
	protected FontRenderer font;

	private int baseWidth, baseHeight, width, height;

	private float xOffset, yOffset;

	private boolean dragging = false;

	private Port<?> draggingPort = null;

	private Map<Port<?>, float[]> relativePortBounds = new HashMap<>();

	private float endY;

	private boolean removed;

	private boolean selected;

	private TextContainer error;

	public GuiScriptComponent(GuiEditScript gui, IDungeonScriptComponent component) {
		this.component = component;
		this.gui = gui;
		this.font = gui.mc.fontRendererObj;

		this.initSize();

		this.width = this.baseWidth;
		this.height = this.baseHeight;

		if(this.isAdditionalInfoOpen()) {
			int[] area = this.getAdditionalArea();
			this.width = Math.max(this.baseWidth, area[0]);
			this.height = this.baseHeight + area[1];
		}

		this.setPortBounds();

		if(component.getError() != null) {
			this.error = new TextContainer(this.getWidth(), 500, component.getError(), this.font, false);
			this.error.setCurrentColor(0xFFFF0000);
			this.error.parse();
		}
	}

	protected void initSize() {
		int[] additionalArea = this.getAdditionalArea();

		int heightPerPort = 12;
		int widthPerPort = 12;

		this.baseHeight = Math.max(this.component.getInputs().size(), this.component.getOutputs().size()) * heightPerPort + 2 + 14;

		int widthInputs = 0;
		int widthOutputs = 0;
		for(InputPort<?> port : this.component.getInputs()) {
			int width = Math.max(this.font.getStringWidth(port.getName()), this.font.getStringWidth(port.getDataType().getSimpleName())) / 2;
			if(width > widthInputs) {
				widthInputs = width;
			}
		}
		for(OutputPort<?> port : this.component.getOutputs()) {
			int width = Math.max(this.font.getStringWidth(port.getName()), this.font.getStringWidth(port.getDataType().getSimpleName())) / 2;
			if(width > widthOutputs) {
				widthOutputs = width;
			}
		}
		this.baseWidth = Math.max(additionalArea[0], Math.max(widthInputs + widthOutputs + 2 * (widthPerPort + 4), this.font.getStringWidth(this.getComponent().getName()) + 4)) + 22;
	}

	protected void setPortBounds() {
		this.relativePortBounds.clear();

		float portWidth = 10;
		float portHeight = 10;

		float yOff = 14;
		for(InputPort<?> port : this.component.getInputs()) {
			this.relativePortBounds.put(port, new float[]{2, yOff, 2 + portWidth, yOff + portHeight});
			yOff += 12;
		}
		if(yOff > this.endY) {
			this.endY = yOff + 1;
		}

		yOff = 14;
		for(OutputPort<?> port : this.component.getOutputs()) {
			this.relativePortBounds.put(port, new float[]{this.baseWidth - 2 - portWidth, yOff, this.baseWidth - 2, yOff + portHeight});
			yOff += 12;
		}
		if(yOff > this.endY) {
			this.endY = yOff + 1;
		}
	}

	public void setRemoved() {
		this.removed = true;
	}

	public boolean isRemoved() {
		return this.removed;
	}

	protected int[] getAdditionalArea() {
		return new int[]{0 ,0};
	}

	public GuiEditScript getGui() {
		return this.gui;
	}

	public IDungeonScriptComponent getComponent() {
		return this.component;
	}

	public int getBaseWidth() {
		return this.baseWidth;
	}

	public int getBaseHeight() {
		return this.baseHeight;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public float getX() {
		return this.component.getGuiX();
	}

	public float getY() {
		return this.component.getGuiY();
	}

	public void setX(float x) {
		this.component.setGuiX(x);
	}

	public void setY(float y) {
		this.component.setGuiY(y);
	}

	public boolean isAdditionalInfoOpen() {
		return this.component.isAdditionalInfoOpen();
	}

	public void setAdditionalInfoOpen(boolean open) {
		this.component.setAdditionalInfoOpen(open);
	}

	public void render(float partialTicks) {
		if(this.dragging) {
			float newX = this.gui.getMouseX() - this.xOffset;
			float newY = this.gui.getMouseY() - this.yOffset;
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				float gridSize = 12.0F;
				newX = (float)Math.floor(newX / gridSize) * gridSize;
				newY = (float)Math.floor(newY / gridSize) * gridSize;
			}
			this.setX(newX);
			this.setY(newY);
		}

		ScaledResolution res = new ScaledResolution(this.gui.mc);

		if(this.getX() + this.getWidth() >= -this.gui.getX() / this.gui.getScale() && this.getY() + this.getHeight() >= -this.gui.getY() / this.gui.getScale()
				&& this.getX() <= (-this.gui.getX() + res.getScaledWidth_double()) / this.gui.getScale() && this.getY() <= (-this.gui.getY() + res.getScaledHeight_double()) / this.gui.getScale()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(this.getX(), this.getY(), 0);

			this.renderComponent(partialTicks);

			GlStateManager.popMatrix();

			if(this.isAdditionalInfoOpen()) {
				GlStateManager.pushMatrix();
				this.renderAdditionalInfo(partialTicks);
				GlStateManager.popMatrix();
			}
		}
	}

	protected void renderComponent(float partialTicks) {
		this.renderBase(partialTicks);
		this.renderPorts(partialTicks);
	}

	protected void renderAdditionalInfo(float partialTicks) {
		GlStateManager.translate(this.getX(), this.getY() + this.endY, 0);
	}

	protected void renderBase(float partialTicks) {
		if(this.isSelected()) {
			Gui.drawRect(-1, -1, this.getWidth()+1, this.getHeight()+1, 0xFFFFFFFF);
		}

		if(this.error != null) {
			Gui.drawRect(-1, -1, this.getWidth()+1, this.getHeight()+1, 0xFFFF0000);

			List<TextPage> pages = this.error.getPages();
			int yOff = 0;
			for(TextPage page : pages) {
				Gui.drawRect(-1, this.getHeight() + 1 + yOff, this.getWidth() + 1, this.getHeight() + 1 + (int)page.getTextHeight() + yOff, 0xDD000000);
				page.render(0, this.getHeight() + 2 + yOff);
				yOff += (int)page.getTextHeight();
			}
		}

		if(this.draggingPort != null) {
			float mx = this.gui.getMouseX() - this.getX();
			float my = this.gui.getMouseY() - this.getY();
			float[] bounds = this.getRelativePortBounds(this.draggingPort);
			CatmullRomSpline spline;
			if(this.draggingPort.isInput()) {
				spline = this.gui.getConnectionSpline(null, mx, my, bounds[0], (bounds[1] + bounds[3]) / 2.0F);
			} else {
				spline = this.gui.getConnectionSpline(null, bounds[2], (bounds[1] + bounds[3]) / 2.0F, mx, my);
			}
			this.gui.drawConnectionSpline(spline, 40);
		}

		Gui.drawRect(0, 0, this.getWidth(), this.getHeight(), 0xFF000000);
		this.drawGradientRect(1, 1, this.getWidth()-1, this.getHeight()-1, 0xFF202020, 0xFF000000);

		this.font.drawString(this.component.getName(), 2, 2, 0xFFFFFFFF);

		Gui.drawRect(this.getWidth() - 23, 2, this.getWidth() - 16, 9, 0xFF303030);
		GlStateManager.pushMatrix();
		GlStateManager.scale(1, 0.75D, 1);
		this.font.drawString("E", this.getWidth() - 22, 4, 0xFFFFFFFF);
		GlStateManager.popMatrix();

		Gui.drawRect(this.getWidth() - 15, 2, this.getWidth() - 10, 9, 0xFF303030);
		GlStateManager.pushMatrix();
		GlStateManager.scale(1, 0.75D, 1);
		this.font.drawString("I", this.getWidth() - 14, 4, 0xFFFFFFFF);
		GlStateManager.popMatrix();

		Gui.drawRect(this.getWidth() - 9, 2, this.getWidth() - 2, 9, 0xFF303030);
		GlStateManager.pushMatrix();
		this.font.drawString("x", this.getWidth() - 8, 1, 0xFFFFFFFF);
		GlStateManager.popMatrix();

		if(this.isAdditionalInfoOpen()) {
			Gui.drawRect(1, (int)this.endY - 2, this.getWidth() - 1, (int)this.endY - 1, 0xFF000000);
		}
	}

	protected void renderPorts(float partialTicks) {
		for(Port<?> port : this.component.getPorts()) {
			float[] bounds = this.getRelativePortBounds(port);
			int color;
			if(port.isInput()) {
				color = !port.isConnected() ? 0xFFFFFF00 : 0xFF00FF00;
				if(((InputPort<?>)port).isRequiredNonNull() && !port.isConnected()) {
					color = 0xFFFF2020;
				}
			} else {
				color = 0xFF0000FF;
			}

			Gui.drawRect((int)bounds[0], (int)bounds[1], (int)bounds[2], (int)bounds[3], color);

			if(port.isInput()) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(bounds[2] + 2, bounds[1], 0);
				GlStateManager.scale(0.5D, 0.5D, 1.0D);
				this.font.drawString(port.getName(), 0, 0, 0xFFFFFFFF);
				this.font.drawString(port.getDataType().getSimpleName(), 0, 10, 0xFFFFFFFF);
				GlStateManager.popMatrix();
			} else {
				int nameWidth = this.font.getStringWidth(port.getName());
				int typeWidth = this.font.getStringWidth(port.getDataType().getSimpleName());

				GlStateManager.pushMatrix();
				GlStateManager.translate(bounds[0] - 2 - nameWidth/2, bounds[1], 0);
				GlStateManager.scale(0.5D, 0.5D, 1.0D);
				this.font.drawString(port.getName(), 0, 0, 0xFFFFFFFF);
				GlStateManager.popMatrix();

				GlStateManager.pushMatrix();
				GlStateManager.translate(bounds[0] - 2 - typeWidth/2, bounds[1], 0);
				GlStateManager.scale(0.5D, 0.5D, 1.0D);
				this.font.drawString(port.getDataType().getSimpleName(), 0, 10, 0xFFFFFFFF);
				GlStateManager.popMatrix();
			}
		}
	}

	public void update() {

	}

	/**
	 * Returns the bounds of the specified port: [minX, minY, maxX, maxY]
	 * @param port
	 * @return
	 */
	public float[] getRelativePortBounds(Port<?> port) {
		return this.relativePortBounds.get(port);
	}

	public boolean isInPort(Port<?> port, float x, float y) {
		float[] bounds = this.getRelativePortBounds(port);
		return x >= bounds[0] + this.getX() && x <= bounds[2] + this.getX() && y >= bounds[1] + this.getY() && y <= bounds[3] + this.getY();
	}

	public boolean isInside(float x, float y) {
		return x >= this.getX() && x <= this.getX() + this.getWidth() && y >= this.getY() && y <= this.getY() + this.getHeight();
	}

	public boolean onMouseClicked(int mouseButton) {

		float mx = this.gui.getMouseX();
		float my = this.gui.getMouseY();

		if(this.isInside(mx, my)) {
			for(Port<?> port : this.component.getPorts()) {
				if(this.isInPort(port, mx, my)) {
					if(mouseButton == 0) {
						this.draggingPort = port;
						this.gui.onPortClicked(this, port);
						return true;
					} else if(mouseButton == 1) {
						port.connect(null);
						return true;
					}
				}
			}

			if(mx >= this.getX() + this.getWidth() - 23 && mx <= this.getX() + this.getWidth() - 16 && my >= this.getY() + 2 && my <= this.getY() + 9) {
				@SuppressWarnings("unchecked")
				IScriptComponentCreationGuiFactory<IDungeonScriptComponent> factory = (IScriptComponentCreationGuiFactory<IDungeonScriptComponent>) ScriptComponentRegistry.INSTANCE.getFactoryGui(this.component.getClass());
				if(factory != null) {
					GuiScreen factoryGui = factory.getFactoryGui(this.gui, (component) -> {
						this.gui.addComponent(component);
						for(InputPort<?> in : this.component.getInputs()) {
							InputPort<?> newInputPort = component.getInput(in.getName());
							if(newInputPort != null) {
								newInputPort.connect(in.getConnectedPort());
							}
						}
						for(OutputPort<?> out : this.component.getOutputs()) {
							OutputPort<?> newOutputPort = component.getOutput(out.getName());
							if(newOutputPort != null) {
								newOutputPort.connect(out.getConnectedPort());
							}
						}
						this.setRemoved();
					}, this.component.getScript(), this.component, this.getX(), this.getY());
					Minecraft.getMinecraft().displayGuiScreen(factoryGui);
					return true;
				}
			}

			if(mx >= this.getX() + this.getWidth() - 15 && mx <= this.getX() + this.getWidth() - 10 && my >= this.getY() + 2 && my <= this.getY() + 9) {
				if(this.isAdditionalInfoOpen()) {
					this.setAdditionalInfoOpen(false);
					this.width = this.baseWidth;
					this.height = this.baseHeight;
					this.setPortBounds();
				} else {
					this.setAdditionalInfoOpen(true);
					int[] area = this.getAdditionalArea();
					this.width = Math.max(this.baseWidth, area[0]);
					this.height = this.baseHeight + area[1];
					this.setPortBounds();
				}
				return true;
			}

			if(mx >= this.getX() + this.getWidth() - 9 && mx <= this.getX() + this.getWidth() - 2 && my >= this.getY() + 2 && my <= this.getY() + 9) {
				this.setRemoved();
				return true;
			}

			if(mouseButton == 0) {
				this.xOffset = mx - this.getX();
				this.yOffset = my - this.getY();
				this.dragging = true;
				this.gui.onStartDragging(this);
				return true;
			}
		}
		return false;
	}

	public void setDragging(boolean dragging) {
		if(dragging) {
			this.xOffset = this.gui.getMouseX() - this.getX();
			this.yOffset = this.gui.getMouseY() - this.getY();
			this.dragging = true;
		} else {
			this.dragging = false;
		}
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void onMouseReleased(int mouseButton) {
		if(mouseButton == 0) {
			this.dragging = false;
			if(this.draggingPort != null) {
				this.gui.onPortReleased(this, this.draggingPort);
				this.draggingPort = null;
			}
		}
	}

	public boolean keyTyped(char typedChar, int keyCode) {
		return false;
	}
}
