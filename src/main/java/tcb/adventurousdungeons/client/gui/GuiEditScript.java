package tcb.adventurousdungeons.client.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import tcb.adventurousdungeons.AdventurousDungeons;
import tcb.adventurousdungeons.ModInfo;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.ILocalDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.component.impl.ScriptDC;
import tcb.adventurousdungeons.api.script.IDungeonScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponent;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Port;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.gui.GuiScriptComponent;
import tcb.adventurousdungeons.api.script.impl.constants.BlockStateConstantSC;
import tcb.adventurousdungeons.api.script.impl.constants.DungeonComponentConstantSC;
import tcb.adventurousdungeons.api.script.impl.constants.ItemStackConstantSC;
import tcb.adventurousdungeons.api.script.impl.subscript.SubScriptImportSC;
import tcb.adventurousdungeons.api.script.impl.subscript.SubScriptSC;
import tcb.adventurousdungeons.common.item.ItemComponentSelection;
import tcb.adventurousdungeons.common.network.common.MessageEditDungeonScript;
import tcb.adventurousdungeons.util.CatmullRomSpline;

public class GuiEditScript extends GuiScreen {
	protected final GuiScreen parent;
	protected final Script script;
	protected final IDungeon dungeon;
	protected final ScriptDC dungeonScriptComponent;
	protected final SubScriptSC subScriptComponent;
	protected final LinkedHashMap<IScriptComponent, GuiScriptComponent> components = new LinkedHashMap<>();

	protected float x, xOffset, y, yOffset;
	protected boolean dragging = false;

	protected boolean selecting;
	protected float selectionX, selectionY;

	protected ScaledResolution res;

	protected float scale = 1.0F;

	protected OutputPort<?> splineDraggingPort;
	protected int splineDraggingCtrlPoint;

	protected GuiTextField subScriptComponentNameField;

	public GuiEditScript(@Nullable GuiScreen parent, IDungeon dungeon, ScriptDC dungeonScriptComponent, Script script, @Nullable SubScriptSC subScriptComponent) {
		this.parent = parent;
		this.script = script;
		this.dungeon = dungeon;
		this.dungeonScriptComponent = dungeonScriptComponent;
		this.subScriptComponent = subScriptComponent;

		/*IScriptComponent comp;
		this.script.addComponent(comp = new SubScriptSC(this.script, "Sub-Script", ImmutableList.of("in_0", "in_1", "in_2", "in_3", "in_4"), ImmutableList.of()));
		comp.initPorts();*/

		//this.script.addComponent(new BlockActivateTriggerSC(this.script, "block_activate"));
		//this.script.addComponent(new SetBlocksSC(this.script, "set_blocks"));
		//this.script.addComponent(new DungeonAreaSC(this.script, "dungeon_area"));
		//this.script.addComponent(new DuplicatorSC(this.script, "dup", 2));
		//this.script.addComponent(new ItemStackConstantSC(this.script, "item", new ItemStack(Items.GOLD_INGOT)));

		//		IScriptComponent trigger = new TriggerSC(this.script, "trigger", TriggerSC.TriggerType.ENTITY_AREA);
		//		IScriptComponent ifElse = new IfElseSC(this.script, "if_else");
		//		IScriptComponent area = new DungeonAreaSC(this.script, "dungeon_area");
		//		IScriptComponent setBlocks = new SetBlocksSC(this.script, "set_blocks");
		//		IScriptComponent merger = new MergerSC(this.script, "merger", 7, 0);
		//		IScriptComponent lst = new IterableSC(this.script, "list", 7);
		//		IScriptComponent iterator = new IteratorSC(this.script, "iterator");
		//		IScriptComponent dup = new DuplicatorSC(this.script, "dup", 2);
		//
		//		this.script.addComponent(trigger);
		//		this.script.addComponent(ifElse);
		//		this.script.addComponent(area);
		//		this.script.addComponent(setBlocks);
		//		this.script.addComponent(merger);
		//		this.script.addComponent(lst);
		//		this.script.addComponent(iterator);
		//		this.script.addComponent(dup);
		//
		//this.script.initPorts();
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getScale() {
		return this.scale;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void initGui() {
		super.initGui();

		this.res = new ScaledResolution(this.mc);

		if(this.components.isEmpty()) {
			for(IScriptComponent component : this.script.getComponents()) {
				if(component instanceof IDungeonScriptComponent) {
					this.components.put(component, ((IDungeonScriptComponent)component).getComponentGui(this));
				}
			}
		}

		this.buttonList.add(new GuiButton(0, this.res.getScaledWidth() - 80, this.res.getScaledHeight() - 20, 78, 18, "Save"));
		this.buttonList.add(new GuiButton(1, this.res.getScaledWidth() - 80, this.res.getScaledHeight() - 40, 78, 18, "Import selected dungeon component"));
		this.buttonList.add(new GuiButton(2, this.res.getScaledWidth() - 80, this.res.getScaledHeight() - 60, 78, 18, "Import block"));
		this.buttonList.add(new GuiButton(3, this.res.getScaledWidth() - 80, this.res.getScaledHeight() - 80, 78, 18, "Import item"));
		this.buttonList.add(new GuiButton(4, this.res.getScaledWidth() - 80, this.res.getScaledHeight() - 100, 78, 18, "Add script component"));
		GuiButton buttonImport;
		this.buttonList.add(buttonImport = new GuiButton(5, this.res.getScaledWidth() - 80, this.res.getScaledHeight() - 120, 78, 18, "Add sub-script import"));
		buttonImport.enabled = this.subScriptComponent != null;
		this.buttonList.add(new GuiButton(6, this.res.getScaledWidth() - 80, this.res.getScaledHeight() - 140, 78, 18, "Merge components"));

		this.selecting = false;
		this.dragging = false;

		if(this.subScriptComponent != null) {
			this.subScriptComponentNameField = new GuiTextField(0, this.fontRendererObj, 2, this.res.getScaledHeight() - 20, 80, 18);
			this.subScriptComponentNameField.setText(this.subScriptComponent.getName());
		}
	}

	public void addComponent(IScriptComponent component) {
		if(component.getInputs().isEmpty() && component.getOutputs().isEmpty()) {
			component.initPorts();
		}
		this.script.addComponent(component);
		if(component instanceof IDungeonScriptComponent) {
			this.components.put(component, ((IDungeonScriptComponent)component).getComponentGui(this));
			((IDungeonScriptComponent)component).setDungeonComponent(this.dungeonScriptComponent);
		}
	}

	public void removeComponent(IScriptComponent component) {
		this.script.removeComponent(component);
		this.components.remove(component);
		if(this.splineDraggingPort != null && this.splineDraggingPort.getComponent() == component) {
			this.splineDraggingPort = null;
			this.splineDraggingCtrlPoint = -1;
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		int scroll = Mouse.getEventDWheel();
		if(scroll != 0) {
			float newScale = MathHelper.clamp(this.scale + (0.1F * Math.signum(scroll)) * this.scale, 0.1F, 1.0F);
			float dx = this.getMouseX() * this.scale / newScale - this.getMouseX();
			float dy = this.getMouseY() * this.scale / newScale - this.getMouseY();
			this.scale = newScale;
			this.x += dx * this.scale;
			this.y += dy * this.scale;
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		IScriptComponent clickedComponent = null;
		for(Entry<IScriptComponent, GuiScriptComponent> entry : this.components.entrySet()) {
			if(entry.getValue().onMouseClicked(mouseButton)) {
				clickedComponent = entry.getKey();
				break;
			}
		}
		if(clickedComponent != null) {
			@SuppressWarnings("unchecked")
			Map<IScriptComponent, GuiScriptComponent> prev = (Map<IScriptComponent, GuiScriptComponent>) this.components.clone();
			prev.remove(clickedComponent);
			GuiScriptComponent gui = this.components.get(clickedComponent);
			this.components.clear();
			this.components.put(clickedComponent, gui);
			this.components.putAll(prev);
		} else {
			if((mouseButton != 0 && mouseButton != 1) || !this.clickSplines(mouseButton == 0)) {
				if(mouseButton == 0) {
					if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
						for(GuiScriptComponent component : this.components.values()) {
							component.setSelected(false);
						}
					}

					this.selecting = true;
					this.selectionX = this.getMouseX();
					this.selectionY = this.getMouseY();
				} else if(mouseButton == 1) {
					this.dragging = true;
					this.xOffset = this.getMouseX();
					this.yOffset = this.getMouseY();
				} else if(mouseButton == 2) {
					this.x = 0;
					this.y = 0;
				}
			}

			if(this.subScriptComponentNameField != null) {
				this.subScriptComponentNameField.mouseClicked(mouseX, mouseY, mouseButton);
			}
		}
	}

	protected boolean clickSplines(boolean leftClick) {
		Vec3d mousePos = new Vec3d(this.getMouseX(), this.getMouseY(), 0);

		for(GuiScriptComponent component : this.components.values()) {
			if(component.getComponent() instanceof IDungeonScriptComponent) {
				IDungeonScriptComponent dungeonScriptComponent = (IDungeonScriptComponent) component.getComponent();
				for(OutputPort<?> port : component.getComponent().getOutputs()) {
					if(port.isConnected()) {
						GuiScriptComponent connectedComponent = this.components.get(port.getConnectedPort().getComponent());
						float[] outputBounds = component.getRelativePortBounds(port);
						float[] inputBounds = connectedComponent.getRelativePortBounds(port.getConnectedPort());
						float x1 = (outputBounds[2]) + component.getX();
						float y1 = (outputBounds[1] + outputBounds[3]) / 2.0F + component.getY();
						float x2 = (inputBounds[0]) + connectedComponent.getX();
						float y2 = (inputBounds[1] + inputBounds[3]) / 2.0F + connectedComponent.getY();

						if((x1 >= -this.x / this.scale && y1 >= -this.y / this.scale
								&& x1 <= (-this.x + this.res.getScaledWidth_double()) / this.scale && y1 <= (-this.y + this.res.getScaledHeight_double()) / this.scale)
								|| (x2 >= -this.x / this.scale && y2 >= -this.y / this.scale
								&& x2 <= (-this.x + this.res.getScaledWidth_double()) / this.scale && y2 <= (-this.y + this.res.getScaledHeight_double()) / this.scale)) {

							List<Vec3d> controlPoints = dungeonScriptComponent.getSplinePoints(port.getName());

							//Check if mouse clicked on an already existing control point
							if(controlPoints != null) {
								for(int i = 0; i < controlPoints.size(); i++) {
									Vec3d ctrlPt = controlPoints.get(i);
									if(mousePos.distanceTo(ctrlPt.addVector(x1, y1, 0)) <= 3) {
										if(leftClick) {
											this.splineDraggingCtrlPoint = i;
											this.splineDraggingPort = port;
										} else {
											controlPoints.remove(i);
											if(controlPoints.isEmpty()) {
												dungeonScriptComponent.setSplinePoints(port.getName(), null);
											} else {
												dungeonScriptComponent.setSplinePoints(port.getName(), controlPoints);
											}
											this.splineDraggingCtrlPoint = -1;
											this.splineDraggingPort = null;
										}
										return true;
									}
								}
							}

							if(leftClick) {
								//Check if mouse is clicking near a spline, and if so create a new control point on spline

								CatmullRomSpline spline = this.getConnectionSpline(port, x1, y1, x2, y2);
								float splineParameter = -1;

								float connectionLength = 0.0F;
								Vec3d prev = spline.getNodes()[0];
								for(Vec3d pt : spline.getNodes()) {
									connectionLength += (float)prev.distanceTo(pt);
									prev = pt;
								}

								int iter = (int)(connectionLength / 10.0F * spline.getNodes().length);
								for(int i = 0; i <= iter; i++) {
									Vec3d pt = spline.interpolate(i / (float)iter);

									if(mousePos.subtract(pt).lengthVector() <= 5.0D) {
										splineParameter = i / (float)iter;
										break;
									}
								}

								if(splineParameter >= 0.0F && splineParameter <= 1.0F) {
									if(controlPoints == null) {
										controlPoints = new ArrayList<>();
									}

									int index = MathHelper.floor(((spline.getNodes().length - 3) * splineParameter));

									controlPoints.add(index, mousePos.addVector(-x1, -y1, 0));
									dungeonScriptComponent.setSplinePoints(port.getName(), controlPoints);

									this.splineDraggingCtrlPoint = index;
									this.splineDraggingPort = port;

									return true;
								}
							}
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		super.mouseReleased(mouseX, mouseY, mouseButton);

		for(GuiScriptComponent component : this.components.values()) {
			component.onMouseReleased(mouseButton);
		}

		this.splineDraggingCtrlPoint = -1;
		this.splineDraggingPort = null;

		if(mouseButton == 0) {
			if(this.selecting) {
				double selectionX2 = this.getMouseX();
				double selectionY2 = this.getMouseY();
				for(GuiScriptComponent component : this.components.values()) {
					if(component.getX() < Math.max(this.selectionX, selectionX2) && component.getX() + component.getWidth() > Math.min(this.selectionX, selectionX2) &&
							component.getY() < Math.max(this.selectionY, selectionY2) && component.getY() + component.getHeight() > Math.min(this.selectionY, selectionY2)) {
						component.setSelected(!component.isSelected());
					}
				}
				this.selecting = false;
			}
		} if(mouseButton == 1) {
			this.dragging = false;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		if(this.subScriptComponent != null) {
			this.fontRendererObj.drawString(this.subScriptComponent.getName(), 2, 2, 0xFFFFFFFF);
		}

		if(this.subScriptComponentNameField != null) {
			this.fontRendererObj.drawString("Name:", 2, this.res.getScaledHeight() - 30, 0xFFFFFFFF);
			this.subScriptComponentNameField.drawTextBox();
		}

		//Update dragged spline control points
		if(this.splineDraggingPort != null && this.splineDraggingCtrlPoint >= 0) {
			IDungeonScriptComponent splineDraggingComponent = (IDungeonScriptComponent) this.splineDraggingPort.getComponent();
			if(splineDraggingComponent != null) {
				List<Vec3d> ctrlPoints = splineDraggingComponent.getSplinePoints(this.splineDraggingPort.getName());
				if(ctrlPoints != null) {
					GuiScriptComponent guiComponent = this.components.get(splineDraggingComponent);
					float[] outputBounds = guiComponent.getRelativePortBounds(this.splineDraggingPort);
					float portX = (outputBounds[2]) + guiComponent.getX();
					float portY = (outputBounds[1] + outputBounds[3]) / 2.0F + guiComponent.getY();
					ctrlPoints.remove(this.splineDraggingCtrlPoint);
					float newX = this.getMouseX();
					float newY = this.getMouseY();
					if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
						float gridSize = 4.0F;
						newX = (float)Math.round(newX / gridSize) * gridSize;
						newY = (float)Math.round(newY / gridSize) * gridSize;
					}
					ctrlPoints.add(this.splineDraggingCtrlPoint, new Vec3d(newX - portX, newY - portY, 0));
					splineDraggingComponent.setSplinePoints(this.splineDraggingPort.getName(), ctrlPoints);
				}
			}
		}

		if(this.dragging) {
			this.x = ((float) (Mouse.getX() * this.res.getScaledWidth_double() / this.mc.displayWidth) / this.scale - this.xOffset) * this.scale;
			this.y = ((float) (this.res.getScaledHeight_double() - Mouse.getY() * this.res.getScaledHeight_double() / this.mc.displayHeight - 1) / this.scale - this.yOffset) * this.scale;
		}

		float mx = this.getMouseX();
		float my = this.getMouseY();

		GlStateManager.pushMatrix();
		GlStateManager.translate(this.x, this.y, 0);
		GlStateManager.scale(this.scale, this.scale, 1);

		for(GuiScriptComponent component : this.components.values()) {
			for(OutputPort<?> port : component.getComponent().getOutputs()) {
				if(port.isConnected()) {
					GuiScriptComponent connectedComponent = this.components.get(port.getConnectedPort().getComponent());
					float[] outputBounds = component.getRelativePortBounds(port);
					float[] inputBounds = connectedComponent.getRelativePortBounds(port.getConnectedPort());
					float x1 = (outputBounds[2]) + component.getX();
					float y1 = (outputBounds[1] + outputBounds[3]) / 2.0F + component.getY();
					float x2 = (inputBounds[0]) + connectedComponent.getX();
					float y2 = (inputBounds[1] + inputBounds[3]) / 2.0F + connectedComponent.getY();
					if((x1 >= -this.x / this.scale && y1 >= -this.y / this.scale
							&& x1 <= (-this.x + this.res.getScaledWidth_double()) / this.scale && y1 <= (-this.y + this.res.getScaledHeight_double()) / this.scale)
							|| (x2 >= -this.x / this.scale && y2 >= -this.y / this.scale
							&& x2 <= (-this.x + this.res.getScaledWidth_double()) / this.scale && y2 <= (-this.y + this.res.getScaledHeight_double()) / this.scale)) {
						CatmullRomSpline spline = this.getConnectionSpline(port, x1, y1, x2, y2);
						this.drawConnectionSpline(spline, spline.getNodes().length - 3 + Math.max(0, (int)(12 * spline.getNodes().length * (this.scale - 0.1D))));
					}
				}
			}
		}

		List<GuiScriptComponent> reversed = new ArrayList<>(this.components.values());
		ListIterator<GuiScriptComponent> it = reversed.listIterator(reversed.size());
		while(it.hasPrevious()) {
			GuiScriptComponent component = it.previous();
			component.render(partialTicks);
		}

		if(this.selecting) {
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableTexture2D();

			GlStateManager.glBegin(GL11.GL_LINE_STRIP);
			GlStateManager.glVertex3f(this.selectionX, this.selectionY, 0);
			GlStateManager.glVertex3f(mx, this.selectionY, 0);
			GlStateManager.glVertex3f(mx, my, 0);
			GlStateManager.glVertex3f(this.selectionX, my, 0);
			GlStateManager.glVertex3f(this.selectionX, this.selectionY, 0);
			GlStateManager.glEnd();

			GlStateManager.enableTexture2D();
		}

		GlStateManager.popMatrix();

		this.fontRendererObj.drawString(I18n.format("gui." + ModInfo.ID + ".select_item_with_number"), this.res.getScaledWidth() / 2 - 60, this.res.getScaledHeight() - 32, 0xFFFFFFFF);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		Iterator<GuiScriptComponent> it = this.components.values().iterator();
		while(it.hasNext()) {
			GuiScriptComponent component = it.next();
			if(component.isRemoved()) {
				for(Port<?> port : component.getComponent().getPorts()) {
					port.connect(null);
				}
				this.script.removeComponent(component.getComponent());
				it.remove();
			} else {
				component.update();
			}
		}

		if(this.subScriptComponentNameField != null) {
			this.subScriptComponentNameField.updateCursorCounter();
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		for(GuiScriptComponent component : this.components.values()) {
			if(component.keyTyped(typedChar, keyCode)) {
				return;
			}
		}

		if(Character.isDigit(typedChar)) {
			int num = Character.getNumericValue(typedChar);
			if(num > 0 && num <= 9) {
				EntityPlayer player = Minecraft.getMinecraft().player;
				if(player != null) {
					player.inventory.currentItem = num - 1;
				}
			}
		}

		if(GuiScreen.isKeyComboCtrlC(keyCode)) {
			List<IScriptComponent> selectedComponents = new ArrayList<>();

			//Get selected components
			for(GuiScriptComponent gui : this.components.values()) {
				if(gui.isSelected()) {
					selectedComponents.add(gui.getComponent());
				}
			}

			//Export components to NBT
			NBTTagCompound nbt = Script.exportComponents(selectedComponents);

			//Write to byte array
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			CompressedStreamTools.writeCompressed(nbt, bao);

			//Set clipboard
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new DataHandler(bao.toByteArray(), "application/octet-stream"), null);
		} else if(GuiScreen.isKeyComboCtrlV(keyCode)) {
			Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

			//Find correct data flavor
			for(DataFlavor flavor : transferable.getTransferDataFlavors()) {
				if("application/octet-stream".equals(flavor.getMimeType())) {
					try {
						Object data = transferable.getTransferData(flavor);

						if(data != null && data.getClass() == byte[].class) {
							//Read NBT from bytes
							ByteArrayInputStream bai = new ByteArrayInputStream((byte[]) data);
							NBTTagCompound nbt = CompressedStreamTools.readCompressed(bai);

							if(nbt != null) {
								List<IScriptComponent> components = Script.importComponents(this.script, nbt, true);

								if(!components.isEmpty()) {
									//Calculate component offset to center
									float minX = Float.MAX_VALUE;
									float minY = Float.MAX_VALUE;
									for(IScriptComponent component : components) {
										if(component instanceof IDungeonScriptComponent) {
											IDungeonScriptComponent dungeonScriptComponent = (IDungeonScriptComponent) component;
											minX = Math.min(minX, dungeonScriptComponent.getGuiX());
											minY = Math.min(minY, dungeonScriptComponent.getGuiY());
										}
									}

									//Unselect all components
									for(GuiScriptComponent gui : this.components.values()) {
										gui.setSelected(false);
									}

									//Add components
									for(IScriptComponent component : components) {
										if(component instanceof IDungeonScriptComponent) {
											IDungeonScriptComponent dungeonScriptComponent = (IDungeonScriptComponent) component;
											dungeonScriptComponent.setGuiX((this.res.getScaledWidth() / 2 - this.x) / this.scale + dungeonScriptComponent.getGuiX() - minX);
											dungeonScriptComponent.setGuiY((this.res.getScaledHeight() / 2 - this.y) / this.scale + dungeonScriptComponent.getGuiY() - minY);
										}

										this.addComponent(component);

										//Select copied component
										GuiScriptComponent gui = this.components.get(component);
										if(gui != null) {
											gui.setSelected(true);
										}
									}
								}
							}
						}
					} catch(Exception ex) { }
				}
			}
		}

		if(keyCode == Keyboard.KEY_ESCAPE) {
			GuiYesNo guiYesNo = new GuiYesNo((result, id) -> {
				int mouseX = Mouse.getX();
				int mouseY = Mouse.getY();
				if(result) {
					this.mc.displayGuiScreen(this.parent);
				} else {
					this.mc.displayGuiScreen(this);
				}
				Mouse.setCursorPosition(mouseX, mouseY);
			}, new TextComponentTranslation("gui." + ModInfo.ID + ".exit_not_saved").getFormattedText(), "", 0) {
				@Override
				public void initGui() {
					super.initGui();
					this.setButtonDelay(20);
					this.buttonList.get(1).enabled = true;
				}

				@Override
				protected void keyTyped(char typedChar, int keyCode) throws IOException {
					super.keyTyped(typedChar, keyCode);

					if(keyCode == Keyboard.KEY_ESCAPE) {
						int mouseX = Mouse.getX();
						int mouseY = Mouse.getY();
						this.mc.displayGuiScreen(GuiEditScript.this);
						Mouse.setCursorPosition(mouseX, mouseY);
					}
				}
			};
			int mouseX = Mouse.getX();
			int mouseY = Mouse.getY();
			this.mc.displayGuiScreen(guiYesNo);
			Mouse.setCursorPosition(mouseX, mouseY);
		} else {
			if(this.subScriptComponentNameField != null && this.subScriptComponentNameField.isFocused()) {
				this.subScriptComponentNameField.textboxKeyTyped(typedChar, keyCode);
			}
		}
	}


	public void onPortClicked(GuiScriptComponent component, Port<?> port) {

	}

	public void onStartDragging(GuiScriptComponent component) {
		for(GuiScriptComponent c : this.components.values()) {
			if(c != component && c.isSelected()) {
				c.setDragging(true);
			}
		}
	}

	public void onPortReleased(GuiScriptComponent component, Port<?> port) {
		float mx = this.getMouseX();
		float my = this.getMouseY();
		loop: 
			for(GuiScriptComponent c : this.components.values()) {
				if(c.isInside(mx, my) && c != component) {
					for(Port<?> p : c.getComponent().getPorts()) {
						if(c.isInPort(p, mx, my)) {
							p.connect(port);
							break loop;
						}
					}
				}
			}
	}

	public float getMouseX() {
		return ((float) (Mouse.getX() * this.res.getScaledWidth_double() / this.mc.displayWidth) - this.x) / this.scale;
	}

	public float getMouseY() {
		return ((float) (this.res.getScaledHeight_double() - Mouse.getY() * this.res.getScaledHeight_double() / this.mc.displayHeight - 1) - this.y) / this.scale;
	}

	public void drawConnectionSpline(CatmullRomSpline spline, int subdivs) {
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();

		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		GL11.glEnable(GL11.GL_LINE_SMOOTH);

		GlStateManager.color(0, 0, 0, 1);
		GlStateManager.glLineWidth(3F);

		GL11.glPointSize(3);

		//GL11.glBegin(GL11.GL_POINTS);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		for(int i = 0; i <= subdivs; i++) {
			float sp = 1.0F / subdivs * (i - 1);
			float s = 1.0F / subdivs * i;
			float segmentLength = 1.0F / (spline.getNodes().length - 3);
			if(MathHelper.floor(sp / segmentLength) < MathHelper.floor(s / segmentLength)) {
				s = Math.min(segmentLength * MathHelper.floor(s / segmentLength), 1);
			}
			Vec3d p = spline.interpolate(s);
			GL11.glVertex2d(p.xCoord, p.yCoord);
		}
		GL11.glEnd();

		GlStateManager.glLineWidth(1.5F);

		GL11.glBegin(GL11.GL_LINE_STRIP);
		for(int i = 0; i <= subdivs; i++) {
			float sp = 1.0F / subdivs * (i - 1);
			float s = 1.0F / subdivs * i;
			float segmentLength = 1.0F / (spline.getNodes().length - 3);
			if(MathHelper.floor(sp / segmentLength) < MathHelper.floor(s / segmentLength)) {
				s = Math.min(segmentLength * MathHelper.floor(s / segmentLength), 1);
			}
			GlStateManager.color(0, s, 1.0F - s, 1);
			Vec3d p = spline.interpolate(s);
			GL11.glVertex2d(p.xCoord, p.yCoord);
		}
		GL11.glEnd();

		int index = 0;
		for(Vec3d pt : spline.getNodes()) {
			if(index > 1 && index < spline.getNodes().length - 2) {
				Gui.drawRect((int)pt.xCoord - 3, (int)pt.yCoord - 3, (int)pt.xCoord + 1, (int)pt.yCoord + 1, 0xFF000000);
				Gui.drawRect((int)pt.xCoord - 2, (int)pt.yCoord - 2, (int)pt.xCoord + 0, (int)pt.yCoord + 0, 0xFFFFFFFF);
			}
			index++;
		}
	}

	public CatmullRomSpline getConnectionSpline(@Nullable OutputPort<?> port, double x, double y, double x2, double y2) {
		Vec3d p1 = new Vec3d(x, y, 0);
		Vec3d p2 = new Vec3d(x2, y2, 0);

		//double mul = p2.xCoord - p1.xCoord < 0 ? 2.5D : 1;

		//double xOff = Math.min(Math.abs(p2.xCoord - p1.xCoord) * mul, 1000);

		double xOff = 0.1F + Math.min(Math.abs(p1.xCoord - p2.xCoord) / (1.0D / Math.pow(Math.abs(p1.yCoord - p2.yCoord) / 10.0D, 1.5D)) * 4.0D, 100) / 1.5D;

		List<Vec3d> splinePtsList = new ArrayList<>();

		splinePtsList.add(p1.add(new Vec3d(/*-xOff*/-xOff, /*(p2.yCoord - p1.yCoord)*/0, 0))/*.subtract(new Vec3d(Math.abs(p2.xCoord - p1.xCoord) * mul + 100, p1.yCoord - p2.yCoord, 0))*/);
		splinePtsList.add(p1);
		if(port != null && port.getComponent() instanceof IDungeonScriptComponent) {
			List<Vec3d> additionalPts = ((IDungeonScriptComponent) port.getComponent()).getSplinePoints(port.getName());
			if(additionalPts != null) {
				for(Vec3d pt : additionalPts) {
					splinePtsList.add(pt.addVector(x, y, 0.0D));
				}
				//TODO
			}
		}
		splinePtsList.add(p2);
		splinePtsList.add(p2.add(new Vec3d(/*xOff*/xOff, /*-(p2.yCoord - p1.yCoord)*/0, 0))/*.subtract(new Vec3d(Math.abs(p2.xCoord - p1.xCoord) * mul + 100, p1.yCoord - p2.yCoord, 0))*/);

		Vec3d[] splinePts = splinePtsList.toArray(new Vec3d[0]);

		return new CatmullRomSpline(splinePts);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if(button.id == 0) {
			this.saveScript();
		}

		if(button.id == 1) {
			this.addSelectedDungeonComponent();
		}

		if(button.id == 2) {
			this.addSelectedBlock();
		}

		if(button.id == 3) {
			this.addSelectedItemStack();
		}

		if(button.id == 4) {
			this.addNewComponent();
		}

		if(button.id == 5) {
			this.addSubScriptImport();
		}

		if(button.id == 6) {
			this.mergeSelectedComponents();
		}
	}

	protected void saveScript() {
		if(this.subScriptComponent == null) {
			AdventurousDungeons.getNetwork().sendToServer(MessageEditDungeonScript.createServerbound(this.dungeon.getID(), this.dungeonScriptComponent.getID(), this.script));
		} else {
			this.subScriptComponent.setSubScript(this.script);
			this.subScriptComponent.setName(this.subScriptComponentNameField.getText());
		}
		this.mc.displayGuiScreen(this.parent);
	}

	protected void addSelectedDungeonComponent() {
		EntityPlayer player = this.mc.player;
		if(player != null) {
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			if(!stack.isEmpty() && stack.getItem() instanceof ItemComponentSelection) {
				ILocalDungeonComponent selected = ((ItemComponentSelection)stack.getItem()).getSelectedComponent(player.world, stack);
				if(selected == null) {
					this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("gui." + ModInfo.ID + ".no_component_selection"));
				} else if(selected.getDungeon() != this.dungeon) {
					this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("gui." + ModInfo.ID + ".component_wrong_dungeon"));
				} else {
					IDungeonScriptComponent component = new DungeonComponentConstantSC(this.script, selected.getName(), selected);
					this.addComponent(component);
					component.setGuiX((this.res.getScaledWidth() / 2 - this.x) / this.scale);
					component.setGuiY((this.res.getScaledHeight() / 2 - this.y) / this.scale);
				}
			} else {
				this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("gui." + ModInfo.ID + ".no_component_selection_item"));
			}
		}
	}

	protected void addSelectedBlock() {
		EntityPlayer player = this.mc.player;
		if(player != null) {
			RayTraceResult ray = player.world.rayTraceBlocks(player.getPositionEyes(1), player.getPositionEyes(1).add(player.getLookVec().scale(6)), true);
			IDungeonScriptComponent component;
			IBlockState block;
			if(ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
				block = player.world.getBlockState(ray.getBlockPos());
			} else {
				block = Blocks.AIR.getDefaultState();
			}
			this.addComponent(component = new BlockStateConstantSC(this.script, block.getBlock().getLocalizedName(), block));
			component.setGuiX((this.res.getScaledWidth() / 2 - this.x) / this.scale);
			component.setGuiY((this.res.getScaledHeight() / 2 - this.y) / this.scale);
		}
	}

	protected void addSelectedItemStack() {
		EntityPlayer player = this.mc.player;
		if(player != null) {
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			IDungeonScriptComponent component = new ItemStackConstantSC(this.script, stack.getDisplayName(), stack);
			component.setGuiX((this.res.getScaledWidth() / 2 - this.x) / this.scale);
			component.setGuiY((this.res.getScaledHeight() / 2 - this.y) / this.scale);
			this.addComponent(component);
		}
	}

	protected void addNewComponent() {
		this.mc.displayGuiScreen(new GuiAddScriptComponents(this, this.script, (this.res.getScaledWidth() / 2 - this.x) / this.scale, (this.res.getScaledHeight() / 2 - this.y) / this.scale));
	}

	protected void addSubScriptImport() {
		List<String> imports = this.subScriptComponent.getImports();
		SubScriptImportSC component = new SubScriptImportSC(this.script, "Import", imports);
		component.setGuiX((this.res.getScaledWidth() / 2 - this.x) / this.scale);
		component.setGuiY((this.res.getScaledHeight() / 2 - this.y) / this.scale);
		this.addComponent(component);
	}

	protected void mergeSelectedComponents() {
		List<IScriptComponent> selectedComponents = new ArrayList<>();

		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;

		//Get selected components
		for(GuiScriptComponent gui : this.components.values()) {
			if(gui.isSelected()) {
				selectedComponents.add(gui.getComponent());
				minX = Math.min(minX, gui.getX());
				minY = Math.min(minY, gui.getY());
			}
		}

		//Export components to new script
		Script subScript = new Script();
		List<IScriptComponent> copies = Script.importComponents(subScript, Script.exportComponents(selectedComponents), true);
		for(IScriptComponent copy : copies) {
			subScript.addComponent(copy);
		}

		//Offset all components to origin
		for(IScriptComponent component : subScript.getComponents()) {
			if(component instanceof IDungeonScriptComponent) {
				IDungeonScriptComponent dungeonScriptComponent = (IDungeonScriptComponent) component;
				dungeonScriptComponent.setGuiX(dungeonScriptComponent.getGuiX() - minX + 12);
				dungeonScriptComponent.setGuiY(dungeonScriptComponent.getGuiY() - minY + 12);
			}
		}

		//Add sub script component
		//TODO Add GUI for inputs and outputs
		SubScriptSC subScriptComponent = new SubScriptSC(this.script, "Sub-Script", ImmutableList.of(), ImmutableList.of());
		subScriptComponent.setSubScript(subScript);
		subScriptComponent.setGuiX((this.res.getScaledWidth() / 2 - this.x) / this.scale);
		subScriptComponent.setGuiY((this.res.getScaledHeight() / 2 - this.y) / this.scale);
		this.addComponent(subScriptComponent);
	}
}
